package com.khangmoihocit.VocabFlow.modules.user.controllers;

import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.modules.user.dtos.request.*;
import com.khangmoihocit.VocabFlow.modules.user.dtos.response.AuthenticationResponse;
import com.khangmoihocit.VocabFlow.modules.user.dtos.response.UserResponse;
import com.khangmoihocit.VocabFlow.modules.user.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "AuthenticationController")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request){
        AuthenticationResponse authenticationResponse = authenticationService.authentication(request);
        ApiResponse<AuthenticationResponse> response =
                ApiResponse.success(authenticationResponse, "Đăng nhập thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@Valid @RequestBody UserCreationRequest request){
        ApiResponse<UserResponse> response =
                ApiResponse.success(authenticationService.register(request), "Tạo tài khoản thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request){
        ApiResponse<?> response =
                ApiResponse.success(authenticationService.refreshToken(request));
        log.info("đang refresh token");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequest request){
        authenticationService.logout(request);
        ApiResponse<?> response = ApiResponse.success("Đăng xuất thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-register")
    ResponseEntity<?> verifyRegister(@Valid @RequestBody VerifyRegisterRequest request){
        AuthenticationResponse result = authenticationService.verifyRegister(request.getEmail(), request.getOtpCode());
        ApiResponse<?> response = ApiResponse.success(result, "Xác thực email thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authenticationService.forgetPassword(request.getEmail());
        ApiResponse<?> response = ApiResponse.success("Mã OTP khôi phục mật khẩu đã được gửi đến email của bạn!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getEmail(), request.getOtpCode(), request.getNewPassword());
        ApiResponse<?> response = ApiResponse.success("Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recover-account")
    ResponseEntity<?> recoverAccount(@Valid @RequestBody RecoverAccountRequest request) {
        authenticationService.recoverAccount(request.getEmail());
        ApiResponse<?> response = ApiResponse.success(
                "Tài khoản của bạn đã được khôi phục thành công! Vui lòng đăng nhập lại."
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/re-new-account")
    ResponseEntity<?> reNewAccount(@Valid @RequestBody RecoverAccountRequest request) {
        authenticationService.reNewAccount(request.getEmail());
        ApiResponse<?> response = ApiResponse.success(
                "Bây giờ bạn có thể đăng nhập lại."
        );
        return ResponseEntity.ok(response);
    }

}
