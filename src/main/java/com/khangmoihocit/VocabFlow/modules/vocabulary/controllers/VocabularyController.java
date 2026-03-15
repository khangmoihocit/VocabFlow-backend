package com.khangmoihocit.VocabFlow.modules.vocabulary.controllers;

import com.khangmoihocit.VocabFlow.core.response.ApiResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.UserSaveWordRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.UserSavedWordResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.UserSavedWordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/vocabularies")
public class VocabularyController {
    UserSavedWordService userSavedWordService;

    @PostMapping
    ResponseEntity<?> savedWordToUser(@Valid @RequestBody UserSaveWordRequest request){
        ApiResponse<UserSavedWordResponse> response =
                ApiResponse.success(userSavedWordService.savedWord(request), "Lưu từ vựng vào sổ tay của bạn thành công!");
        return ResponseEntity.ok(response);
    }
}
