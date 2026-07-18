package com.railtrack.train.service.impl;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import com.railtrack.train.client.RailRadarClient;
import com.railtrack.train.dto.request.TrainSearchHistoryRequest;
import com.railtrack.train.dto.response.*;
import com.railtrack.train.mapper.RailRadarMapper;
import com.railtrack.train.service.TrainSearchHistoryService;
import com.railtrack.train.service.TrainService;
import com.railtrack.train.util.TrainRecommendationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Single implementation for all RailRadar-backed train functionality: AI
 * recommendation, train details/live-status/route, journeys between
 * stations, and station boards. Backed by the single RailRadarClient, with
 * every provider payload passed through {@link RailRadarMapper} before it
 * leaves this service - callers never see raw RailRadar JSON. The legacy
 * search provider is still used internally by {@link #getRecommendedTrain}
 * to source AI train recommendations, but is no longer exposed as its own
 * public search endpoint.
 */
@Service
public class TrainServiceImpl implements TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final RailRadarClient client;
    private final TrainRecommendationUtil recommendationUtil;
    private final SearchHistoryService searchHistoryService;
    private final TrainSearchHistoryService trainSearchHistoryService;
    private final RailRadarMapper mapper;

    public TrainServiceImpl(
            RailRadarClient client,
            TrainRecommendationUtil recommendationUtil,
            SearchHistoryService searchHistoryService,
            TrainSearchHistoryService trainSearchHistoryService,
            RailRadarMapper mapper) {

        this.client = client;
        this.recommendationUtil = recommendationUtil;
        this.searchHistoryService = searchHistoryService;
        this.trainSearchHistoryService = trainSearchHistoryService;
        this.mapper = mapper;
    }

    @Override
    public RecommendedTrainResponse getRecommendedTrain(String from, String to) {

        TrainSearchResponse response = client.legacySearchTrains(from, to);

        Train bestTrain = recommendationUtil.getBestTrain(
                response.getData().getTrains());

        return new RecommendedTrainResponse(bestTrain, response);
    }

    @Override
    public TrainDetailsResponse trainDetails(String number, boolean haltsOnly) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.trainDetails(number, haltsOnly);
        record(SearchOperation.TRAIN_DETAILS, number, response);
        logDuration("trainDetails", number, start);
        return mapper.mapTrainDetails(response);
    }

    @Override
    public LiveTrainResponse liveTrain(String number, LocalDate date,
                                       boolean haltsOnly, boolean geometry,
                                       String format, boolean includeCoordinates) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.liveTrain(number, date, haltsOnly, geometry, format, includeCoordinates);
        record(SearchOperation.LIVE_TRAIN_STATUS, number, response);
        logDuration("liveTrain", number, start);
        return mapper.mapLiveTrain(response);
    }

    @Override
    public TrainRouteResponse route(String number, String format, boolean stops) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.route(number, format, stops);
        record(SearchOperation.TRAIN_ROUTE_GEOMETRY, number, response);
        logDuration("route", number, start);
        return mapper.mapTrainRoute(response);
    }

    @Override
    public JourneyResponse betweenStations(String from, String to, LocalDate date,
                                           boolean live, boolean byCity,
                                           String type, String category,
                                           String quota, String travelClass) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.betweenStations(from, to, date, live,
                byCity, type, category);

        if (response != null && response.success()) {
            searchHistoryService.save(SearchOperation.JOURNEY_BETWEEN_STATIONS, from, to);
            if (date != null && travelClass != null && quota != null) {
                trainSearchHistoryService.saveSearch(new TrainSearchHistoryRequest(
                        from, to, date, travelClass, quota));
            }
        }
        logDuration("betweenStations", from + "->" + to, start);

        // 1. Assign mapped response to a variable instead of returning it immediately
        JourneyResponse journeyResponse = mapper.mapBetweenStations(response, from, to);

        // 2. Filter list based on day of week if date information is present
        if (date != null && journeyResponse != null && journeyResponse.getTrains() != null) {

            // Get standard short day name (e.g., "Sat", "Tue")
            String targetDay = date.getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

            // 3. Match against TrainSummaryResponse runningDays list elements cleanly
            List<com.railtrack.train.dto.response.TrainSummaryResponse> filteredTrains =
                    journeyResponse.getTrains().stream()
                            .filter(train -> {
                                // If runningDays array is missing or empty, keep the train as safe fallback
                                if (train.getRunningDays() == null || train.getRunningDays().isEmpty()) {
                                    return true;
                                }

                                // Matches if the train runs on this specific day (e.g. "Sat") or is "Daily"
                                return train.getRunningDays().contains(targetDay) ||
                                        train.getRunningDays().contains("Daily") ||
                                        train.getRunningDays().contains("All");
                            })
                            .collect(Collectors.toList());

            journeyResponse.setTrains(filteredTrains);
        }

        // 4. Return the filtered journey object at the very end of the execution block
        return journeyResponse;
    }

    @Override
    public StationBoardResponse stationBoard(String code, boolean includeIntermediate) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.stationBoard(code, includeIntermediate);
        record(SearchOperation.STATION_BOARD, code, response);
        logDuration("stationBoard", code, start);
        return mapper.mapStationBoard(response, code);
    }

    @Override
    public LiveStationBoardResponse stationLiveBoard(String code, int hours,
                                                      boolean includeIntermediate) {
        long start = System.currentTimeMillis();
        RailRadarResponse response = client.stationLiveBoard(code, hours, includeIntermediate);
        record(SearchOperation.STATION_LIVE_BOARD, code, response);
        logDuration("stationLiveBoard", code, start);
        return mapper.mapLiveStationBoard(response, code);
    }

    private void record(SearchOperation operation, String identifier, RailRadarResponse response) {
        if (response != null && response.success()) {
            searchHistoryService.save(operation, identifier, null);
        }
    }

    private void logDuration(String operation, String identifier, long startMillis) {
        log.info("RailRadar {} completed for {} in {}ms",
                operation, identifier, System.currentTimeMillis() - startMillis);
    }
}
