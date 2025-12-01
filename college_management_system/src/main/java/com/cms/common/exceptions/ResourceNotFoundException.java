package com.cms.common.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 * Example: Role, Driver, User, etc.
 */
public class ResourceNotFoundException extends RuntimeException {

    // Constructor with message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Optional: constructor with message + cause
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
