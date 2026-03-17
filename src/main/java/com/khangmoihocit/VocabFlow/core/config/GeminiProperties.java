package com.khangmoihocit.VocabFlow.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Đọc toàn bộ cấu hình Gemini từ application.yml (prefix: gemini)
 */
@Data
@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    /** Danh sách tất cả API key (tối đa 10) */
    private List<String> apiKeys;

    /** Tên model Gemini sẽ dùng */
    private String model = "gemini-2.5-flash-lite";

    /** Thời gian (giây) một key bị "đóng băng" sau khi gặp lỗi rate-limit/quota */
    private long keyCooldownSeconds = 60;
}
