package com.cms.transport.route.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cms.common.ApiResponse;
import com.cms.common.exceptions.ResourceNotFoundException;

@Slf4j
@RestControllerAdvice
public class RouteGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("❗ Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleInvalid(IllegalArgumentException ex) {
        log.warn("❗ Bad request: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneral(Exception ex) {
        log.error("🔥 Internal error: ", ex);
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.fail("Something went wrong!"));
    }
}
