package com.railtrack.dashboard.dto;

import java.time.LocalDateTime;

/**
 * Lightweight response DTO for a recent AI history record on the dashboard.
 */
public class DashboardAiHistoryResponse {

    private Long id;
    private String question;
    private LocalDateTime createdAt;

    public DashboardAiHistoryResponse(Long id,
                                      String question,
                                      LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
