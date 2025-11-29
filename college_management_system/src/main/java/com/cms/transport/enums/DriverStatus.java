package com.cms.transport.enums;

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

    // -------------------------
    // Assignment & Duty States
    // -------------------------
    AVAILABLE("AVAILABLE"),       // Not assigned to any bus
    ASSIGNED("ASSIGNED"),         // Currently assigned to a bus/route
    ON_DUTY("ON_DUTY"),           // Actively driving right now
    OFF_DUTY("OFF_DUTY"),         // End of shift but still active employee

    // -------------------------
    // Safety & Special Conditions
    // -------------------------
    TRAINING("TRAINING"),         // Under training or certification
    UNFIT("UNFIT"),               // Not fit for duty (medical/accident reasons)

    // -------------------------
    // For any unforeseen cases
    // -------------------------
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
