package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khangmoihocit.VocabFlow.core.response.PageResponse;
import com.khangmoihocit.VocabFlow.core.specification.GenericSpecificationBuilder;
import com.khangmoihocit.VocabFlow.core.utils.SortUtil;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.record.GeminiWordInfo;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.LookupRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.DictionaryWordResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.LookupResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.TranslateResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.DictionaryWord;
import com.khangmoihocit.VocabFlow.modules.vocabulary.mappers.DictionaryWordMapper;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.DictionaryWordRepository;
import com.khangmoihocit.VocabFlow.integration.GeminiChatClientPool;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.DictionaryWordService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "DICTIONARY WORD SERVICE")
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class DictionaryWordServiceImpl implements DictionaryWordService {

    DictionaryWordRepository dictionaryWordRepository;
    GeminiChatClientPool geminiPool;
    RestClient restClient;
    ObjectMapper objectMapper = new ObjectMapper();
    DictionaryWordMapper dictionaryWordMapper;

    public DictionaryWordServiceImpl(DictionaryWordRepository dictionaryWordRepository, GeminiChatClientPool geminiPool, DictionaryWordMapper dictionaryWordMapper) {
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.geminiPool = geminiPool;
        this.dictionaryWordMapper = dictionaryWordMapper;
        this.restClient = RestClient.create();
    }

    @Override
    public LookupResponse lookupBasic(String word) {
        String cleanWord = word.trim().toLowerCase();

        //Kiểm tra Database (Nếu có ai đó từng tra bằng AI rồi thì hưởng sái luôn)
        Optional<DictionaryWord> existingWordOpt = dictionaryWordRepository.findFirstByWord(cleanWord);
        if (existingWordOpt.isPresent()) {
            return dictionaryWordMapper.toLookupResponse(existingWordOpt.get());
        }

        WordData dictData = fetchFromDictionaryApi(cleanWord);
        String finalAudioUrl = (dictData.audioUrl() != null) ? dictData.audioUrl() : generateGoogleTtsUrl(cleanWord);

        DictionaryWord newWord = DictionaryWord.builder()
                .word(cleanWord)
                .partOfSpeech(dictData.partOfSpeech() != null ? dictData.partOfSpeech() : "unknown")
                .pronunciation(dictData.phonetic())
                .meaningVi("Đang cập nhật!")
                .explanationEn(dictData.explanationEn())
                .explanationVi(dictData.explanationVi())
                .exampleSentence(dictData.exampleSentence())
                .audioUrl(finalAudioUrl)
                .build();

        newWord = dictionaryWordRepository.save(newWord);
        return dictionaryWordMapper.toLookupResponse(newWord);
    }

    @Override
    public LookupResponse lookupWithAi(LookupRequest request) {
        String cleanWord = request.getWord().trim().toLowerCase();

        List<DictionaryWord> existingWords = dictionaryWordRepository.findAllByWord(cleanWord);
        Optional<DictionaryWord> completeWordOpt = existingWords.stream()
                .filter(w -> !isDictionaryWordIncomplete(w))
                .findFirst();

        if (completeWordOpt.isPresent()) {
            log.info("Tra từ bằng AI: Từ '{}' đã có đầy đủ data trong DB.", cleanWord);
            return dictionaryWordMapper.toLookupResponse(completeWordOpt.get());
        }

        WordData aiData = fetchFromGeminiApi(cleanWord, request.getContextSentence());
        WordData dictData = fetchFromDictionaryApi(cleanWord);

        String finalAudioUrl = StringUtils.hasText(dictData.audioUrl()) ? dictData.audioUrl() : generateGoogleTtsUrl(cleanWord);
        String finalPhonetic = StringUtils.hasText(dictData.phonetic()) ? dictData.phonetic() : aiData.phonetic();
        String finalExplanationEn = StringUtils.hasText(aiData.explanationEn()) ? aiData.explanationEn() : dictData.explanationEn();

        DictionaryWord wordToSave = existingWords.stream()
                .filter(w -> w.getPartOfSpeech() != null && w.getPartOfSpeech().equalsIgnoreCase(aiData.partOfSpeech()))
                .findFirst()
                .orElse(new DictionaryWord());

        wordToSave.setWord(cleanWord);
        wordToSave.setPartOfSpeech(aiData.partOfSpeech());
        wordToSave.setAudioUrl(finalAudioUrl);

        if (wordToSave.getId() == null ||
                !StringUtils.hasText(wordToSave.getMeaningVi()) ||
                wordToSave.getMeaningVi().contains("Đang cập nhật!")) {

            wordToSave.setPronunciation(finalPhonetic);
            wordToSave.setMeaningVi(aiData.meaningVi());
            wordToSave.setExplanationEn(finalExplanationEn);
            wordToSave.setExplanationVi(aiData.explanationVi());
            wordToSave.setExampleSentence(aiData.exampleSentence());
        }

        DictionaryWord savedWord = dictionaryWordRepository.save(wordToSave);

        return dictionaryWordMapper.toLookupResponse(savedWord);
    }

    private boolean isDictionaryWordIncomplete(DictionaryWord word) {
        if (word == null) return true;

        return !StringUtils.hasText(word.getPartOfSpeech()) || "unknown".equalsIgnoreCase(word.getPartOfSpeech())
                || !StringUtils.hasText(word.getPronunciation())
                || !StringUtils.hasText(word.getMeaningVi()) || word.getMeaningVi().contains("Đang cập nhật!")
                || !StringUtils.hasText(word.getExplanationEn())
                || !StringUtils.hasText(word.getExplanationVi())
                || !StringUtils.hasText(word.getExampleSentence());
    }

    @Override
    public TranslateResponse translateText(String text) {
        String prompt = String.format(
                "Hãy đóng vai một chuyên gia ngôn ngữ. Dịch đoạn văn tiếng Anh sau sang tiếng Việt " +
                        "một cách tự nhiên, trôi chảy và sát nghĩa nhất. " +
                        "Chỉ trả về duy nhất kết quả dịch, tuyệt đối không giải thích hay thêm ký tự thừa:\n\n\"%s\"",
                text
        );

        String translatedText = geminiPool.callWithFallback(
                client -> client.prompt().user(u -> u.text(prompt)).call().entity(String.class));

        if (translatedText.startsWith("\"") && translatedText.endsWith("\"")) {
            translatedText = translatedText.substring(1, translatedText.length() - 1);
        }

        return TranslateResponse.builder()
                .originalText(text)
                .translatedText(translatedText)
                .build();
    }

    @Override
    public PageResponse<DictionaryWordResponse> findAll(int pageNo, int pageSize, String sortParam, String keyword) {
        Sort sort = SortUtil.createSort(sortParam);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        GenericSpecificationBuilder<DictionaryWord> builder = new GenericSpecificationBuilder<>();
        builder.with("word", "=", keyword);
        Specification<DictionaryWord> specification = builder.build();

        Page<DictionaryWord> dictionaryWordPage = dictionaryWordRepository.findAll(specification, pageable);

        return null;
    }

    private WordData fetchFromDictionaryApi(String word) {
        String phonetic = null;
        String audioUrl = null;
        String partOfSpeech = null;
        String meaningVi = "Không tìm thấy nghĩa trong từ điển gốc"; // Sẽ bị Gemini ghi đè sau
        String explanationEn = null;
        String explanationVi = "";
        String exampleSentence = "";

        try {
            var response = restClient.get()
                    .uri("https://api.dictionaryapi.dev/api/v2/entries/en/{word}", word)
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode body = objectMapper.readTree(response.getBody());
                if (body.isArray() && !body.isEmpty()) {
                    JsonNode firstEntry = body.get(0);

                    // Lấy âm thanh và phiên âm
                    if (firstEntry.has("phonetic") && !firstEntry.get("phonetic").isNull()) {
                        phonetic = firstEntry.get("phonetic").asText();
                    }
                    JsonNode phoneticsArray = firstEntry.get("phonetics");
                    if (phoneticsArray != null && phoneticsArray.isArray()) {
                        for (JsonNode node : phoneticsArray) {
                            if (phonetic == null && node.has("text")) phonetic = node.get("text").asText();
                            if (node.has("audio") && !node.get("audio").asText().isEmpty()) {
                                audioUrl = node.get("audio").asText();
                                break;
                            }
                        }
                    }

                    JsonNode meaningsArray = firstEntry.get("meanings");
                    if (meaningsArray != null && meaningsArray.isArray() && !meaningsArray.isEmpty()) {
                        JsonNode firstMeaning = meaningsArray.get(0);
                        if (firstMeaning.has("partOfSpeech")) partOfSpeech = firstMeaning.get("partOfSpeech").asText();
                        JsonNode definitions = firstMeaning.get("definitions");
                        if (definitions != null && definitions.isArray() && !definitions.isEmpty()) {
                            explanationEn = definitions.get(0).get("definition").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Dictionary API bỏ qua từ: {}", word);
        }
        return new WordData(partOfSpeech, phonetic, meaningVi, explanationEn, explanationVi, exampleSentence, audioUrl);
    }

    private WordData fetchFromGeminiApi(String word, String contextSentence) {
            GeminiWordInfo aiInfo = geminiPool.callWithFallback(client -> client.prompt()
                .user(u -> u.text("Bạn là chuyên gia ngôn ngữ. Phân tích '{word}' trong câu: '{context}'. Trả về JSON với các key: partOfSpeech, phonetic, meaningVi, explanationEn, explanationVi, exampleSentence.")
                        .param("word", word)
                        .param("context", contextSentence != null ? contextSentence : ""))
                .call()
                .entity(GeminiWordInfo.class));

        return new WordData(aiInfo.partOfSpeech(), aiInfo.phonetic(), aiInfo.meaningVi(), aiInfo.explanationEn(),
                aiInfo.explanationVi(), aiInfo.exampleSentence(), null);
    }

    private String generateGoogleTtsUrl(String text) {
        try {
            // Mã hóa URL để chống lỗi dấu cách (VD: "get ahead" -> "get+ahead")
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            return "https://translate.google.com/translate_tts?ie=UTF-8&q=" + encodedText + "&tl=en&client=tw-ob";
        } catch (Exception e) {
            return null;
        }
    }

    private record WordData(String partOfSpeech, String phonetic, String meaningVi,
                            String explanationEn, String explanationVi, String exampleSentence, String audioUrl) {}
}