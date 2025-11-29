package com.cms.common.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DriverStatus {
    ACTIVE("ACTIVE"),         // Currently employed and available for duty
    INACTIVE("INACTIVE"),     // Not employed or permanently inactive
    SUSPENDED("SUSPENDED"),   // Temporarily suspended (disciplinary or administrative reasons)
    ON_LEAVE("ON_LEAVE");     // Temporarily on leave (medical, personal, etc.)

    private final String value;

    DriverStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DriverStatus fromValue(String value) {
        for (DriverStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown driver status: " + value);
    }
}
