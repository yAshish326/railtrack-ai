package com.railtrack.dashboard.dto;

import com.railtrack.auth.dto.response.UserResponse;

import java.util.List;

/**
 * Response DTO containing authenticated-user dashboard data.
 */
public class DashboardResponse {

    private UserResponse user;
    private DashboardStatsResponse stats;
    private List<DashboardPnrResponse> recentPnrSearches;
    private List<DashboardAiHistoryResponse> recentAiHistory;

    public DashboardResponse(UserResponse user,
                             DashboardStatsResponse stats,
                             List<DashboardPnrResponse> recentPnrSearches,
                             List<DashboardAiHistoryResponse> recentAiHistory) {
        this.user = user;
        this.stats = stats;
        this.recentPnrSearches = recentPnrSearches;
        this.recentAiHistory = recentAiHistory;
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
