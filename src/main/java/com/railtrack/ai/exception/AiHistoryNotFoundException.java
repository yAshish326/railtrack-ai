package com.railtrack.ai.exception;

/**
 * Exception thrown when an AI history record cannot be found for the user.
 */
public class AiHistoryNotFoundException extends RuntimeException {

    public AiHistoryNotFoundException(String message) {
        super(message);
    }
}
