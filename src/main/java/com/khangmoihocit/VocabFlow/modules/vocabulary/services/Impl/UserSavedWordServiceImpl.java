package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.security.UserDetailsCustom;
import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import com.khangmoihocit.VocabFlow.modules.user.repositories.UserRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.UserSaveWordRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.UserSavedWordResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.DictionaryWord;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.UserSavedWord;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.DictionaryWordRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.UserSavedWordRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.UserSavedWordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "USER SAVED WORD SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSavedWordServiceImpl implements UserSavedWordService {
    UserSavedWordRepository userSavedWordRepository;
    UserRepository userRepository;
    DictionaryWordRepository dictionaryWordRepository;

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
                .orElseThrow(()->new AppException(ErrorCode.VOCABULARY_NOT_FOUND));

        UserSavedWord userSavedWord = UserSavedWord.builder()
                .user(user)
                .dictionaryWord(dictionaryWord)
                .contextSentence(request.getSourceSentence())
                .sourceUrl(request.getSourceUrl())
                .build();
        userSavedWord = userSavedWordRepository.save(userSavedWord);
        return UserSavedWordResponse.builder().userSavedWordId(userSavedWord.getId()).build();
    }
}
