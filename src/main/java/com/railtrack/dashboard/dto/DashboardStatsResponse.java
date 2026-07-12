package com.railtrack.dashboard.dto;

/**
 * Response DTO containing user-scoped dashboard statistics.
 */
public class DashboardStatsResponse {

    private Integer totalPnrSearches;
    private Integer totalAiRequests;

    public DashboardStatsResponse(Integer totalPnrSearches,
                                  Integer totalAiRequests) {
        this.totalPnrSearches = totalPnrSearches;
        this.totalAiRequests = totalAiRequests;
    }

    public Integer getTotalPnrSearches() {
        return totalPnrSearches;
    }

    public void setTotalPnrSearches(Integer totalPnrSearches) {
        this.totalPnrSearches = totalPnrSearches;
    }

    public Integer getTotalAiRequests() {
        return totalAiRequests;
    }

    public void setTotalAiRequests(Integer totalAiRequests) {
        this.totalAiRequests = totalAiRequests;
    }
}
