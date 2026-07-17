package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

/** Business-level response for a single stop on a train's route. */
@Data
@Builder
public class RouteStationResponse {

    private Integer sequence;

    private String stationCode;

    private String stationName;

    private Integer dayNumber;

    private Double distanceKm;

    private String arrival;

    private String departure;

    private Integer haltMinutes;

    private String platform;

    private Double latitude;

    private Double longitude;

    private boolean currentStation;
}
