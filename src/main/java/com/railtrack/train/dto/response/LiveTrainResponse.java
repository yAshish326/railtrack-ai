package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

/** Business-level response for live train status. */
@Data
@Builder
public class LiveTrainResponse {

    private String trainNumber;

    private String trainName;

    private String previousStation;

    private String currentStation;

    private String nextStation;

    private Double latitude;

    private Double longitude;

    private Integer delayMinutes;

    private String expectedArrival;

    private String actualArrival;

    private String platform;

    private Double speedKmph;

    private String runningStatus;

    private String lastUpdatedAt;
}
