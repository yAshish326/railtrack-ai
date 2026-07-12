package com.railtrack.pnr.exception;

/**
 * Exception thrown when a PNR history record is unavailable to the user.
 */
public class PnrHistoryNotFoundException extends RuntimeException {

    public PnrHistoryNotFoundException(String message) {
        super(message);
    }
}
