package com.railtrack.train.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
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
    private Boolean currentStation;
}