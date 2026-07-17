package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

/** A single train row on a station board (static or live). */
@Data
@Builder
public class StationBoardTrainResponse {

    private String trainNumber;

    private String trainName;

    private String arrival;

    private String departure;

    private String expectedArrival;

    private String expectedDeparture;

    private Integer delayMinutes;

    private String platform;

    private String status;
}
