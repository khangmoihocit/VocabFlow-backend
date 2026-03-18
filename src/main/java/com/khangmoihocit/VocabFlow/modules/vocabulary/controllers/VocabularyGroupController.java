package com.khangmoihocit.VocabFlow.modules.vocabulary.controllers;

import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.VocabularyGroupRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.TopicResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.VocabularyGroupResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.VocabularyGroup;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.VocabularyGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "TOPIC CONTROLLER")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/vocabulary-groups")
public class VocabularyGroupController {
    VocabularyGroupService vocabularyGroupService;

    @GetMapping("/find-all")
    ResponseEntity<?> findAll(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                              @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
                              @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        PageResponse<VocabularyGroupResponse> result = vocabularyGroupService.findAll(pageNo, pageSize, sort, keyword);
        ApiResponse<PageResponse<VocabularyGroupResponse>> response = ApiResponse.success(result, "tải danh sách nhóm từ vựng bạn tạo thành công!");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody VocabularyGroupRequest request) {
        VocabularyGroupResponse data = vocabularyGroupService.create(request);
        ApiResponse<VocabularyGroupResponse> response = ApiResponse.success(data, "tạo bộ từ vựng thành công");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> update(@Valid @RequestBody VocabularyGroupRequest request,
                             @PathVariable Long id) {
        VocabularyGroupResponse data = vocabularyGroupService.update(request, id);
        ApiResponse<VocabularyGroupResponse> response = ApiResponse.success(data, "cập nhật bộ từ vựng thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        vocabularyGroupService.deleteById(id);
        ApiResponse<VocabularyGroupResponse> response = ApiResponse.success("xóa bộ từ vựng thành công");
        return ResponseEntity.ok(response);
    }
}
