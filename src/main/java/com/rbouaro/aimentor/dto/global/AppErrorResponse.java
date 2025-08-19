package com.rbouaro.aimentor.dto.global;

public record AppErrorResponse<T>(
        String message,
        String code,
        T details
) {
}