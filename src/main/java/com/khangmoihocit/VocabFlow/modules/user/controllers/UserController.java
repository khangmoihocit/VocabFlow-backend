package com.khangmoihocit.VocabFlow.modules.user.controllers;

import com.google.protobuf.Api;
import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.core.exception.OurException;
import com.khangmoihocit.VocabFlow.core.services.CloudinaryService;
import com.khangmoihocit.VocabFlow.modules.user.dtos.response.UserResponse;
import com.khangmoihocit.VocabFlow.modules.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/user")
public class UserController {
    UserService userService;
    long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vui lòng chọn một file ảnh!"));
        }

        //Kiểm tra định dạng file (Chỉ cho phép JPG, PNG)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Định dạng không hợp lệ! Chỉ chấp nhận ảnh JPG hoặc PNG."));
        }

        //Kiểm tra kích thước file (Tối đa 5MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Kích thước ảnh quá lớn! Vui lòng chọn ảnh dưới 5MB."));
        }

        try{
            String avatarUrl = userService.uploadAvatar(file);
            return ResponseEntity.ok(ApiResponse.success(avatarUrl));
        }catch (OurException ex){
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAll(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                                                   @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                                                                   @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
                                                                   @RequestParam(name = "keyword", defaultValue = "") String keyword){
        PageResponse<UserResponse> pageResponse = userService.getUsers(pageNo, pageSize, sort, keyword);
        ApiResponse<PageResponse<UserResponse>> response =
                ApiResponse.success(pageResponse, pageResponse.getData().isEmpty() ? "Danh sách user trống" : "Lấy danh sách thành công");

        return ResponseEntity.ok(response);
    }
}
