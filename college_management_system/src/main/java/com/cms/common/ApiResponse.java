package com.cms.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from JSON
public class ApiResponse {

    private boolean success; // true if operation succeeded, false otherwise
    private String message;  // descriptive message
    private Object data;     // optional data payload

    /**
     * Static helper for success response
     */
    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Static helper for failure response
     */
    public static ApiResponse fail(String message, Object data) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Static helper for failure response without data
     */
    public static ApiResponse fail(String message) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Static helper for success response without data
     */
    public static ApiResponse success(String message) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .build();
    }
}
