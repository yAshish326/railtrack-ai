package com.railtrack.common.exception;

public class RailwayApiException extends RuntimeException {

    public RailwayApiException(String message) {
        super(message);
    }

    public RailwayApiException(String message, Throwable cause) {
        super(message, cause);
    }
}