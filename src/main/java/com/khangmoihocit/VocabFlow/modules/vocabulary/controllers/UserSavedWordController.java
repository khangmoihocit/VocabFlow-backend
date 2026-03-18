package com.khangmoihocit.VocabFlow.modules.vocabulary.controllers;

import com.google.protobuf.Api;
import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.UserSaveWordRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.UserSavedWordResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.WordSavedFindResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.UserSavedWordRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.UserSavedWordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "USER SAVED WORD CONTROLLER")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/user-saved-words")
public class UserSavedWordController {
    UserSavedWordService userSavedWordService;

    @PostMapping
    ResponseEntity<?> savedWordToUser(@Valid @RequestBody UserSaveWordRequest request){
        ApiResponse<UserSavedWordResponse> response =
                ApiResponse.success(userSavedWordService.savedWord(request), "Lưu từ vựng vào sổ tay của bạn thành công!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-all/{vocabularyGroupId}")
    ResponseEntity<?> findWordSaveUser(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                                       @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
                                       @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                       @PathVariable Long vocabularyGroupId){
        PageResponse<WordSavedFindResponse> result = userSavedWordService.findSaveWordByUser(pageNo, pageSize, sort, keyword, vocabularyGroupId);
        ApiResponse<PageResponse<WordSavedFindResponse>> response = ApiResponse.success(result, "tải danh sách từ trong database thành công!");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteById(@PathVariable Long id){
        userSavedWordService.deleteBySavedWordId(id);
        ApiResponse<?> response = ApiResponse.success("Xóa từ vựng khỏi sổ tay của bạn thành công!");
        return ResponseEntity.ok(response);
    }
}
