package com.cms.transport.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BusStatus {

    ACTIVE("ACTIVE"),            // Bus is available and running
    IN_MAINTENANCE("IN_MAINTENANCE"), // Bus is under maintenance
    OUT_OF_SERVICE("OUT_OF_SERVICE"), // Bus is temporarily not operational
    INACTIVE("INACTIVE");        // Bus is decommissioned or retired

    private final String value;

    BusStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BusStatus fromValue(String value) {
        for (BusStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BusStatus: " + value);
    }
}
