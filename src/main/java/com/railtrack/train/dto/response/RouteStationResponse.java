package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteStationResponse {

    private Integer sequence;

    private String stationCode;

    private String stationName;

    private Integer day;

    private Double distance;

    private Integer arrival;

    private Integer departure;

    private Integer haltMinutes;
}