package com.cms.transport.enums;





import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransportStatus {

    ACTIVE,
    INACTIVE;
 

    // Converts enum → JSON
    @JsonValue
    public String toJson() {
        return name().toLowerCase();   // output = "active"
    }

    // Converts JSON → enum safely
    @JsonCreator
    public static TransportStatus fromJson(String value) {
        if (value == null) return null;

        try {
            return TransportStatus.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid transport status: " + value);
        }
    }
}
