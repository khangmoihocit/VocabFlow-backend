package com.khangmoihocit.VocabFlow.modules.youtube_learning.controllers;

import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.AttemptRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.UserSegmentAttemptResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.UserSegmentAttemptService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/attempts")
public class UserSegmentAttemptController {
    UserSegmentAttemptService userSegmentAttemptService;

    /**
     * API nhận danh sách kết quả Dictation từ Frontend,
     * tự động chấm điểm và lưu vào database.
     */
    @PostMapping("/dictation")
    public ResponseEntity<?> submitDictationAttempts(
            @Valid @RequestBody List<AttemptRequest> attempts) {
        List<UserSegmentAttemptResponse> responses = userSegmentAttemptService.save(attempts);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
