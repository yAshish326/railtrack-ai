package com.railtrack.dashboard.dto;

/**
 * Response DTO containing user-scoped dashboard statistics.
 */
public class DashboardStatsResponse {

    private Integer totalTrainSearches;
    private Integer totalPnrSearches;
    private Integer totalAiRequests;

    public DashboardStatsResponse(Integer totalTrainSearches,
                                  Integer totalPnrSearches,
                                  Integer totalAiRequests) {
        this.totalTrainSearches = totalTrainSearches;
        this.totalPnrSearches = totalPnrSearches;
        this.totalAiRequests = totalAiRequests;
    }

    public Integer getTotalTrainSearches() {
        return totalTrainSearches;
    }

    public void setTotalTrainSearches(Integer totalTrainSearches) {
        this.totalTrainSearches = totalTrainSearches;
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
