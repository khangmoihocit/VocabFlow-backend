package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.mapper.PageMapper;
import com.khangmoihocit.VocabFlow.core.security.UserDetailsCustom;
import com.khangmoihocit.VocabFlow.core.specification.GenericSpecificationBuilder;
import com.khangmoihocit.VocabFlow.core.utils.SortUtil;
import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import com.khangmoihocit.VocabFlow.modules.user.repositories.UserRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.UserSaveWordRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.UserSavedWordResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.WordSavedFindResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.DictionaryWord;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.UserSavedWord;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.VocabularyGroup;
import com.khangmoihocit.VocabFlow.modules.vocabulary.mappers.UserSavedWordMapper;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.DictionaryWordRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.UserSavedWordRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.VocabularyGroupRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.UserSavedWordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j(topic = "USER SAVED WORD SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSavedWordServiceImpl implements UserSavedWordService {
    UserSavedWordRepository userSavedWordRepository;
    UserRepository userRepository;
    DictionaryWordRepository dictionaryWordRepository;
    VocabularyGroupRepository vocabularyGroupRepository;
    UserSavedWordMapper userSavedWordMapper;
    PageMapper pageMapper;

    @Override
    @Transactional
    public UserSavedWordResponse savedWord(UserSaveWordRequest request) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userSavedWordRepository.existsByUserIdAndDictionaryWordId(userDetails.getId(), request.getDictionaryWordId())) {
            throw new AppException(ErrorCode.VOCABULARY_ALREADY_EXISTS);
        }
        //getReferenceById: tạo đối tượng giả chỉ lấy id
        User user = userRepository.getReferenceById(userDetails.getId());
        DictionaryWord dictionaryWord = dictionaryWordRepository.findById(request.getDictionaryWordId())
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_FOUND));
        VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findById(request.getVocabularyGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_GROUP_NOT_EXISTS));

        UserSavedWord userSavedWord = UserSavedWord.builder()
                .user(user)
                .dictionaryWord(dictionaryWord)
                .vocabularyGroup(vocabularyGroup)
                .contextSentence(request.getSourceSentence())
                .sourceUrl(request.getSourceUrl())
                .build();
        userSavedWord = userSavedWordRepository.save(userSavedWord);
        return UserSavedWordResponse.builder().userSavedWordId(userSavedWord.getId()).build();
    }

    @Override
    public PageResponse<WordSavedFindResponse> findSaveWordByUser(int pageNo, int pageSize, String sort,
                                                                  String keyword, Long vocabularyGroupId) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, SortUtil.createSort(sort));

        GenericSpecificationBuilder<UserSavedWord> builder = new GenericSpecificationBuilder<>();
        builder.withJoinById("user", userDetails.getId());
        builder.withJoinById("vocabularyGroup", vocabularyGroupId);
        if (StringUtils.hasText(keyword)) {// kiểm tra null và khoảng trắng
            builder.withJoin("dictionaryWord", "word", "=", keyword.trim());
        }

        Specification<UserSavedWord> specification = builder.build();

        Page<UserSavedWord> savedWordPage = userSavedWordRepository.findAll(specification, pageable);

        List<WordSavedFindResponse> data = userSavedWordMapper.toListWordSavedFindResponse(savedWordPage.getContent());

        return pageMapper.toPageResponse(savedWordPage, data);
    }
}
