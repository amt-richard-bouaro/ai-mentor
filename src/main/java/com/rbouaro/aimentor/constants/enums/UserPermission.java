package com.rbouaro.aimentor.constants.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserPermission {
    USER, ADMIN;


    @JsonCreator
    public static UserPermission fromString(String key) {
        return UserPermission.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}