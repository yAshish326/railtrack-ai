package com.railtrack.auth.exception;

/**
 * Exception thrown when the authenticated user cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
