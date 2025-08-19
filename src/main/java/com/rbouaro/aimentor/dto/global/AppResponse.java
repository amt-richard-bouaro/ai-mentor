package com.rbouaro.aimentor.dto.global;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppResponse<T>(
        String message,
        T data
) {
}