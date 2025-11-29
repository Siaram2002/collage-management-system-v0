package com.cms.common.enums;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ACTIVE("ACTIVE"),         // Admin is active and can log in
    INACTIVE("INACTIVE"),     // Admin account is disabled or deactivated
    SUSPENDED("SUSPENDED"),   // Temporarily blocked due to policy violation
    ON_LEAVE("ON_LEAVE");     // Temporarily unavailable (vacation, medical, etc.)

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Status fromValue(String value) {
        for (Status status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown admin status: " + value);
    }
}
