package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Business-level summary of a single train, used by between-stations search results. */
@Data
@Builder
public class TrainSummaryResponse {

    private String trainNumber;

    private String trainName;

    private String trainType;

    private String source;

    private String destination;

    private String departure;

    private String arrival;

    private String duration;

    private Double distanceKm;

    private List<String> runningDays;

    private List<String> availableClasses;
}
