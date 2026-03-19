package com.khangmoihocit.VocabFlow.modules.user.controllers;

import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.user.dtos.response.UserResponse;
import com.khangmoihocit.VocabFlow.modules.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/user")
public class UserController {
    UserService userService;

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
