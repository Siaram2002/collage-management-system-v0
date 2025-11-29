package com.cms.students.enums;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnrollmentStatus {
    ACTIVE("ACTIVE"),
    GRADUATED("GRADUATED"),
    DROPPED("DROPPED"),
    SUSPENDED("SUSPENDED"),
    ON_LEAVE("ON_LEAVE");

    private final String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EnrollmentStatus fromValue(String value) {
        for (EnrollmentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enrollment status: " + value);
    }
}
