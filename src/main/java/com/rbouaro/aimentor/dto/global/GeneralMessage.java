package com.rbouaro.aimentor.dto.global;

public record GeneralMessage<T>(
        String message,
        T data

) {
}