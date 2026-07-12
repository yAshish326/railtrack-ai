package com.railtrack.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.pnr.dto.response.PnrHistoryResponse;

import java.util.List;
import java.util.Map;

/**
 * Response DTO containing authenticated-user dashboard data.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardResponse {

    private UserResponse user;
    private List<PnrHistoryResponse> recentPnrSearches;
    private List<AiHistoryResponse> recentAiHistory;
    private Map<String, Object> additionalSections;

    public DashboardResponse(UserResponse user,
                             List<PnrHistoryResponse> recentPnrSearches,
                             List<AiHistoryResponse> recentAiHistory,
                             Map<String, Object> additionalSections) {
        this.user = user;
        this.recentPnrSearches = recentPnrSearches;
        this.recentAiHistory = recentAiHistory;
        this.additionalSections = additionalSections;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public List<PnrHistoryResponse> getRecentPnrSearches() {
        return recentPnrSearches;
    }

    public void setRecentPnrSearches(
            List<PnrHistoryResponse> recentPnrSearches) {
        this.recentPnrSearches = recentPnrSearches;
    }

    public List<AiHistoryResponse> getRecentAiHistory() {
        return recentAiHistory;
    }

    public void setRecentAiHistory(List<AiHistoryResponse> recentAiHistory) {
        this.recentAiHistory = recentAiHistory;
    }

    public Map<String, Object> getAdditionalSections() {
        return additionalSections;
    }

    public void setAdditionalSections(
            Map<String, Object> additionalSections) {
        this.additionalSections = additionalSections;
    }
}
