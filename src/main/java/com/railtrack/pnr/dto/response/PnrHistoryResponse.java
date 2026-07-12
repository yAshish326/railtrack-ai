package com.railtrack.pnr.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO representing a user's PNR search history record.
 */
public class PnrHistoryResponse {

    private Long id;

    private String pnrNumber;

    private String trainNumber;

    private String trainName;

    private String sourceStation;

    private String destinationStation;

    private String journeyClass;

    private String chartStatus;

    private LocalDateTime searchedAt;

    public PnrHistoryResponse() {
    }

    public PnrHistoryResponse(Long id,
                              String pnrNumber,
                              String trainNumber,
                              String trainName,
                              String sourceStation,
                              String destinationStation,
                              String journeyClass,
                              String chartStatus,
                              LocalDateTime searchedAt) {
        this.id = id;
        this.pnrNumber = pnrNumber;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.journeyClass = journeyClass;
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

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
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

    public String getJourneyClass() {
        return journeyClass;
    }

    public void setJourneyClass(String journeyClass) {
        this.journeyClass = journeyClass;
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
