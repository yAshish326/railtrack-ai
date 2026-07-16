package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainDetailsResponse {

    private String trainNumber;
    private String trainName;
    private String trainType;

    private String sourceStationCode;
    private String sourceStationName;

    private String destinationStationCode;
    private String destinationStationName;

    private Integer totalHalts;

    private Double distanceKm;

    private Integer travelTimeMinutes;

    private List<String> runningDays;
}