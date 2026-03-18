package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.DictionaryWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnkiConnectService {

    private final HttpClient ankiHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ANKI_URL = "http://127.0.0.1:8765";

    public void createDeck(String deckName) {
        Map<String, Object> payload = Map.of(
                "action", "createDeck",
                "version", 6,
                "params", Map.of("deck", deckName)
        );

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ANKI_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            ankiHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Lỗi khi tạo deck {}: {}", deckName, e.getMessage());
        }
    }

    // Hàm thêm thẻ vào Anki
    public Long addNote(String deckName, DictionaryWord word) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("Word", word.getWord());
        fields.put("PartOfSpeech", word.getPartOfSpeech() != null ? word.getPartOfSpeech() : "");
        fields.put("Pronunciation", word.getPronunciation() != null ? word.getPronunciation() : "");
        fields.put("MeaningVi", word.getMeaningVi() != null ? word.getMeaningVi() : "");
        fields.put("ExplanationEn", word.getExplanationEn() != null ? word.getExplanationEn() : "");
        fields.put("ExplanationVi", word.getExplanationVi() != null ? word.getExplanationVi() : "");
        fields.put("ExampleSentence", word.getExampleSentence() != null ? word.getExampleSentence() : "");

        Map<String, Object> note = new HashMap<>();
        note.put("deckName", deckName);
        note.put("modelName", "VocabFlow_Model");
        note.put("fields", fields);
        note.put("options", Map.of("allowDuplicate", false));

        if (word.getAudioUrl() != null && !word.getAudioUrl().isEmpty()) {
            note.put("audio", List.of(Map.of(
                    "url", word.getAudioUrl(),
                    "filename", "vocabflow_" + word.getWord().replaceAll("[^a-zA-Z0-9]", "") + ".mp3",
                    "fields", List.of("Audio")
            )));
        }

        Map<String, Object> payload = Map.of(
                "action", "addNote",
                "version", 6,
                "params", Map.of("note", note)
        );

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ANKI_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = ankiHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> body = objectMapper.readValue(responseBody, Map.class);
                if (body.get("error") != null) {
                    log.error("❌ Anki từ chối từ '{}': {}", word.getWord(), body.get("error"));
                    return null;
                }
                if (body.get("result") != null) {
                    return Long.valueOf(body.get("result").toString());
                }
            }
        } catch (Exception e) {
            log.error("Lỗi HTTP khi gọi AnkiConnect addNote: {}", e.getMessage());
            throw new RuntimeException("Không thể kết nối AnkiConnect: " + e.getMessage(), e);
        }
        return null;
    }
}
