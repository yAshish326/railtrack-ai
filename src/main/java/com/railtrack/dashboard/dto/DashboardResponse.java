package com.railtrack.dashboard.dto;

import com.railtrack.auth.dto.response.UserResponse;

import java.util.List;

/**
 * Response DTO containing authenticated-user dashboard data.
 */
public class DashboardResponse {

    private UserResponse user;
    private DashboardStatsResponse stats;
    private List<DashboardTrainSearchResponse> recentTrainSearches;
    private List<DashboardPnrResponse> recentPnrSearches;
    private List<DashboardAiHistoryResponse> recentAiHistory;

    public DashboardResponse(UserResponse user,
                             DashboardStatsResponse stats,
                             List<DashboardTrainSearchResponse> recentTrainSearches,
                             List<DashboardPnrResponse> recentPnrSearches,
                             List<DashboardAiHistoryResponse> recentAiHistory) {
        this.user = user;
        this.stats = stats;
        this.recentTrainSearches = recentTrainSearches;
        this.recentPnrSearches = recentPnrSearches;
        this.recentAiHistory = recentAiHistory;
    }

    public List<DashboardTrainSearchResponse> getRecentTrainSearches() {
        return recentTrainSearches;
    }

    public void setRecentTrainSearches(
            List<DashboardTrainSearchResponse> recentTrainSearches) {
        this.recentTrainSearches = recentTrainSearches;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public DashboardStatsResponse getStats() {
        return stats;
    }

    public void setStats(DashboardStatsResponse stats) {
        this.stats = stats;
    }

    public List<DashboardPnrResponse> getRecentPnrSearches() {
        return recentPnrSearches;
    }

    public void setRecentPnrSearches(
            List<DashboardPnrResponse> recentPnrSearches) {
        this.recentPnrSearches = recentPnrSearches;
    }

    public List<DashboardAiHistoryResponse> getRecentAiHistory() {
        return recentAiHistory;
    }

    public void setRecentAiHistory(
            List<DashboardAiHistoryResponse> recentAiHistory) {
        this.recentAiHistory = recentAiHistory;
    }

}
