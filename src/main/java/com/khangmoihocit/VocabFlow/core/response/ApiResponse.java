package com.khangmoihocit.VocabFlow.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private int code;
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
