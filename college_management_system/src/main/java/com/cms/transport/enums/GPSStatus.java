package com.cms.transport.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GPSStatus {

    // -------------------------
    // Availability & Assignment
    // -------------------------
    AVAILABLE("AVAILABLE"),         // Not assigned to any bus -> free to use
    ASSIGNED("ASSIGNED"),           // Assigned to a BusAssignment
    INSTALLED("INSTALLED"),         // Permanently installed on a bus

    // -------------------------
    // Device Health / Connectivity
    // -------------------------
    ACTIVE("ACTIVE"),               // Working normally
    INACTIVE("INACTIVE"),           // Not in use OR turned off
    DISCONNECTED("DISCONNECTED"),   // No signal (network/GPS not reachable)
    MALFUNCTION("MALFUNCTION"),     // Hardware error / internal issues

    // -------------------------
    // Service / Maintenance
    // -------------------------
    MAINTENANCE("MAINTENANCE"),     // Under diagnostics/repair
    REPAIR_REQUIRED("REPAIR_REQUIRED"),  // Fault detected, needs service

    // -------------------------
    // Lifecycle End
    // -------------------------
    RETIRED("RETIRED"),             // Permanently decommissioned

    // -------------------------
    // For unexpected cases
    // -------------------------
    OTHER("OTHER");

    private final String value;

    GPSStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GPSStatus fromValue(String value) {
        for (GPSStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown GPS status: " + value);
    }
}
