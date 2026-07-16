package com.railtrack.train.service;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.TrainSearchResponse;

import java.time.LocalDate;

/**
 * Single domain service for everything RailRadar-backed: legacy train search,
 * AI recommendation, train details/live-status/route, between-station
 * journeys, and station boards.
 */
public interface TrainService {

    // Existing search API
    TrainSearchResponse searchTrains(String from, String to);

    /**
     * Performs the existing train search and records its submitted criteria
     * only after the external search succeeds.
     */
    TrainSearchResponse searchTrains(String from, String to,
                                     LocalDate journeyDate,
                                     String travelClass, String quota);

    // AI recommendation API
    RecommendedTrainResponse getRecommendedTrain(String from, String to);

    // RailRadar train details/live-status/route
    RailRadarResponse trainDetails(String number, LocalDate journeyDate,
                                   String dataType, String dataProvider,
                                   String userId);

    RailRadarResponse liveTrain(String number, LocalDate date, boolean haltsOnly,
                                boolean geometry, String format,
                                boolean includeCoordinates);

    RailRadarResponse route(String number, String format, boolean stops);

    // RailRadar journeys between stations
    RailRadarResponse betweenStations(String from, String to, LocalDate date,
                                      boolean live, boolean byCity, String type,
                                      String category);

    // RailRadar station boards
    RailRadarResponse stationBoard(String code, boolean includeIntermediate);

    RailRadarResponse stationLiveBoard(String code, int hours,
                                       boolean includeIntermediate);
}
