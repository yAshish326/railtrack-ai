package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Business-level response for a train's complete route. */
@Data
@Builder
public class TrainRouteResponse {

    private String trainNumber;

    private String trainName;

    private Double totalDistanceKm;

    private List<RouteStationResponse> stations;
}
