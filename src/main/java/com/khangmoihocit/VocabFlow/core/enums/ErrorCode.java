package com.khangmoihocit.VocabFlow.core.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // ===== GENERAL =====
    UNCATEGORIZED_EXCEPTION("UNCATEGORIZED_EXCEPTION", "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("INVALID_REQUEST", "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    // ===== AUTH =====
    UNAUTHENTICATED("UNAUTHENTICATED", "Please login!", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    ACCOUNT_ALREADY_GOOGLE("ACCOUNT_ALREADY_GOOGLE", "Tài khoản này được đăng ký bằng Google. Vui lòng đăng nhập lại.", HttpStatus.BAD_REQUEST),
    USER_DETAIL_IS_NULL("USER_DETAIL_IS_NULL", "không thể lấy user detail", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_REGISTER("EMAIL_NOT_REGISTER", "email chưa được đăng ký.", HttpStatus.NOT_FOUND),
    OTP_VERIFY_ERROR("OTP_VERIFY_ERROR", "Mã OTP không hợp lệ.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED("USER_ALREADY_VERIFIED", "email này đã được xác thực.", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("OTP_EXPIRED", "Mã otp đã hết hạn.", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERIFY("ACCOUNT_NOT_VERIFY", "Tài khoản chưa được xác thực", HttpStatus.BAD_REQUEST),

    // ===== TOKEN =====
    INVALID_TOKEN("INVALID_TOKEN", "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("REFRESH_TOKEN_REVOKED", "Refresh token đã bị thu hồi", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_LIMIT("REFRESH_TOKEN_LIMIT", "Bạn đang đăng nhập trên 3 thiết bị, các thiết bị trước đó sẽ tự động đăng xuất", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),

    // ===== USER =====
    USER_NOT_FOUND("USER_NOT_FOUND", "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email đã tồn tại", HttpStatus.CONFLICT),
    USER_IS_EMPTY("USER_IS_EMPTY", "Danh sách người dùng trống", HttpStatus.NOT_FOUND),
    ACCOUNT_DELETED_BUT_CAN_RECOVER("ACCOUNT_DELETED_BUT_CAN_RECOVER", "Email này đã từng được sử dụng và bị xóa. Chúng tôi đã gửi link khôi phục đến email của bạn.", HttpStatus.BAD_REQUEST),
    // ===== VALIDATION =====
    VALIDATION_ERROR("VALIDATION_ERROR", "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),

    // ===== VOCABULARY =====
    VOCABULARY_NOT_FOUND("VOCABULARY_NOT_FOUND", "Từ vựng không tồn tại", HttpStatus.NOT_FOUND),
    VOCABULARY_ALREADY_EXISTS("VOCABULARY_ALREADY_EXISTS", "Từ vựng đã tồn tại", HttpStatus.CONFLICT),
    VOCABULARY_GROUP_NOT_EXISTS("VOCABULARY_GROUP_NOT_EXISTS", "Bộ từ vựng không tồn tại", HttpStatus.NOT_FOUND),
    VOCABULARY_DEFAULT_NOT_INIT("VOCABULARY_DEFAULT_NOT_INIT", "Bộ từ vựng mặc định không tồn tại", HttpStatus.NOT_FOUND),

    // ===== SENTENCE PRACTICE =====
    SENTENCE_NOT_FOUND("SENTENCE_NOT_FOUND", "Câu không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_TRANSLATION("INVALID_TRANSLATION", "Bản dịch không chính xác", HttpStatus.BAD_REQUEST),

    // ===== LEARNING =====
    LESSON_NOT_FOUND("LESSON_NOT_FOUND", "Bài học không tồn tại", HttpStatus.NOT_FOUND),
    QUIZ_NOT_FOUND("QUIZ_NOT_FOUND", "Bài kiểm tra không tồn tại", HttpStatus.NOT_FOUND),

    // ===== SYSTEM =====
    DATABASE_ERROR("DATABASE_ERROR", "Lỗi cơ sở dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
