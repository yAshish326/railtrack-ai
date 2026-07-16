package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainRouteResponse {

    private String trainNumber;

    private String trainName;

    private List<RouteStationResponse> stations;
}