package com.railtrack.train.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainRouteResponse {
    private String trainNumber;
    private String trainName;
    private Double totalDistanceKm;
    private List<String> runningDays;
    private List<RouteStationResponse> stations;
}