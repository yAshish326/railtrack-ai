package com.railtrack.train.service;

import com.railtrack.train.dto.response.JourneyResponse;
import com.railtrack.train.dto.response.LiveStationBoardResponse;
import com.railtrack.train.dto.response.LiveTrainResponse;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.StationBoardResponse;
import com.railtrack.train.dto.response.TrainDetailsResponse;
import com.railtrack.train.dto.response.TrainRouteResponse;

import java.time.LocalDate;

/**
 * Single domain service for everything RailRadar-backed: AI recommendation,
 * train details/live-status/route, between-station journeys, and station
 * boards. The legacy between-stations search endpoint has been retired in
 * favor of the RailRadar-backed {@link #betweenStations} as the single
 * public search capability; the legacy provider is still used internally
 * by {@link #getRecommendedTrain} only.
 *
 * <p>Every method returns an application-owned business DTO - never the raw
 * {@code RailRadarResponse}/{@code JsonNode} provider payload.
 */
public interface TrainService {

    // AI recommendation API
    RecommendedTrainResponse getRecommendedTrain(String from, String to);

    // RailRadar train details/live-status/route
    TrainDetailsResponse trainDetails(String number, boolean haltsOnly);

    LiveTrainResponse liveTrain(String number, LocalDate date, boolean haltsOnly,
                                boolean geometry, String format,
                                boolean includeCoordinates);

    TrainRouteResponse route(String number, String format, boolean stops);

    // RailRadar journeys between stations
    JourneyResponse betweenStations(String from, String to, LocalDate date,
                                    boolean live, boolean byCity, String type,
                                    String category, String quota, String travelClass);

    // RailRadar station boards
    StationBoardResponse stationBoard(String code, boolean includeIntermediate);

    LiveStationBoardResponse stationLiveBoard(String code, int hours,
                                              boolean includeIntermediate);
}
