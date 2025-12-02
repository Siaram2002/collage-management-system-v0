package com.cms.transport.driver.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DriverStatus {

    // -------------------------
    // Employment & Lifecycle
    // -------------------------
    ACTIVE("ACTIVE"),             // Employed and available for operations
    INACTIVE("INACTIVE"),         // Resigned / retired / permanently inactive
    SUSPENDED("SUSPENDED"),       // Temporarily suspended
    ON_LEAVE("ON_LEAVE"),         // On leave (medical, personal, etc.)




    OTHER("OTHER");

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
