package com.railtrack.common.exception;

import org.springframework.http.HttpStatus;

/** Translated RailRadar HTTP or transport failure. */
public class RailRadarClientException extends RuntimeException {
    private final HttpStatus status;
    public RailRadarClientException(HttpStatus status, String message, Throwable cause) { super(message, cause); this.status = status; }
    public HttpStatus getStatus() { return status; }
}
