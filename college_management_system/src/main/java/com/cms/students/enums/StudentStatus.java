package com.cms.students.enums;




import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StudentStatus {
    PENDING("PENDING"),
    ENROLLED("ENROLLED"),
    GRADUATED("GRADUATED"),
    DROPPED_OUT("DROPPED_OUT"),
    SUSPENDED("SUSPENDED"),
    ON_LEAVE("ON_LEAVE"),
    EXPELLED("EXPELLED");

    private final String value;

    StudentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StudentStatus fromValue(String value) {
        for (StudentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown student status: " + value);
    }
}
