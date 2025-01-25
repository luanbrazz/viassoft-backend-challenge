package com.viassoft.emailservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StandardError {
    private final LocalDateTime timestamp;
    private final Integer status;
    private final String error;
    private final String message;
    private final String path;
}