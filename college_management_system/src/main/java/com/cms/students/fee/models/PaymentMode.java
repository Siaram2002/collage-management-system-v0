package com.cms.students.fee.models;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMode {

    CASH("cash"),
    ONLINE("online"),
    CARD("card"),
    UPI("upi"),
    CHEQUE("cheque");

    private final String value;

    PaymentMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentMode fromValue(String value) {
        if (value == null) return null;

        for (PaymentMode mode : PaymentMode.values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentMode: " + value);
    }
}
