package com.railtrack.dashboard.dto;

import java.time.LocalDateTime;

/**
 * Lightweight response DTO for a recent PNR search on the dashboard.
 */
public class DashboardPnrResponse {

    private Long id;
    private String pnrNumber;
    private String trainName;
    private String trainNumber;
    private String sourceStation;
    private String destinationStation;
    private String chartStatus;
    private LocalDateTime searchedAt;

    public DashboardPnrResponse(Long id,
                                String pnrNumber,
                                String trainName,
                                String trainNumber,
                                String sourceStation,
                                String destinationStation,
                                String chartStatus,
                                LocalDateTime searchedAt) {
        this.id = id;
        this.pnrNumber = pnrNumber;
        this.trainName = trainName;
        this.trainNumber = trainNumber;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.chartStatus = chartStatus;
        this.searchedAt = searchedAt;
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

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getChartStatus() {
        return chartStatus;
    }

    public void setChartStatus(String chartStatus) {
        this.chartStatus = chartStatus;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(LocalDateTime searchedAt) {
        this.searchedAt = searchedAt;
    }
}
