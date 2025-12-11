package com.cms.busPass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BusPassStatus {
    ACTIVE("ACTIVE"),     
    EXPIRED("EXPIRED"),    
    CANCELLED("CANCELLED"), 
    INACTIVE("INACTIVE"),
    PENDING("PENDING");     

    private final String value;

    BusPassStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BusPassStatus fromValue(String value) {
        for (BusPassStatus status : BusPassStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BusPassStatus: " + value);
    }
}
