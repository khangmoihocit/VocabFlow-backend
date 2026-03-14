package com.khangmoihocit.VocabFlow.core.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(2101, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXIST(2102, "Người dùng không tồn tại", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.status = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode status;
}
