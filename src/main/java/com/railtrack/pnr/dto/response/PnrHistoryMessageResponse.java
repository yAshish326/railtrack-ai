package com.railtrack.pnr.dto.response;

/**
 * Response DTO for PNR history mutation operations.
 */
public class PnrHistoryMessageResponse {

    private String message;

    public PnrHistoryMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
