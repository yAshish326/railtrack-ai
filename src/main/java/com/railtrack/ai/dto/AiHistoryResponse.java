package com.railtrack.ai.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for AI history records.
 */
public class AiHistoryResponse {

    private Long id;
    private String pnrNumber;
    private String aiResponse;
    private LocalDateTime createdAt;

    public AiHistoryResponse() {
    }

    public AiHistoryResponse(Long id,
                             String pnrNumber,
                             String aiResponse,
                             LocalDateTime createdAt) {
        this.id = id;
        this.pnrNumber = pnrNumber;
        this.aiResponse = aiResponse;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
