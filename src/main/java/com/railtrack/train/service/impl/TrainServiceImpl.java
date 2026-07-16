package com.railtrack.train.service.impl;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import com.railtrack.train.client.RailRadarClient;
import com.railtrack.train.dto.request.TrainSearchHistoryRequest;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainSearchHistoryService;
import com.railtrack.train.service.TrainService;
import com.railtrack.train.util.TrainRecommendationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Single implementation for all RailRadar-backed train functionality: legacy
 * search, AI recommendation, train details/live-status/route, journeys
 * between stations, and station boards. Backed by the single RailRadarClient.
 */
@Service
public class TrainServiceImpl implements TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final RailRadarClient client;
    private final TrainRecommendationUtil recommendationUtil;
    private final TrainSearchHistoryService trainSearchHistoryService;
    private final SearchHistoryService searchHistoryService;

    public TrainServiceImpl(
            RailRadarClient client,
            TrainRecommendationUtil recommendationUtil,
            TrainSearchHistoryService trainSearchHistoryService,
            SearchHistoryService searchHistoryService) {

        this.client = client;
        this.recommendationUtil = recommendationUtil;
        this.trainSearchHistoryService = trainSearchHistoryService;
        this.searchHistoryService = searchHistoryService;
    }

    @Override
    public TrainSearchResponse searchTrains(String from, String to) {
        return client.legacySearchTrains(from, to);
    }

    /**
     * Executes the existing external search, then records only the submitted
     * criteria after a successful response has been returned by the client.
     */
    @Override
    public TrainSearchResponse searchTrains(String from, String to,
                                            LocalDate journeyDate,
                                            String travelClass, String quota) {
        TrainSearchResponse response = client.legacySearchTrains(from, to);
        if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
            trainSearchHistoryService.saveSearch(new TrainSearchHistoryRequest(
                    from, to, journeyDate, travelClass, quota));
            searchHistoryService.save(SearchOperation.TRAIN_SEARCH, from, to);
        }
        return response;
    }

    @Override
    public RecommendedTrainResponse getRecommendedTrain(String from, String to) {

        TrainSearchResponse response = client.legacySearchTrains(from, to);

        Train bestTrain = recommendationUtil.getBestTrain(
                response.getData().getTrains());

        return new RecommendedTrainResponse(bestTrain, response);
    }

    @Override
    public RailRadarResponse trainDetails(String number, LocalDate journeyDate,
                                          String dataType, String dataProvider,
                                          String userId) {
        return record(SearchOperation.TRAIN_DETAILS, number,
                client.trainDetails(number, journeyDate, dataType, dataProvider, userId));
    }

    @Override
    public RailRadarResponse liveTrain(String number, LocalDate date,
                                       boolean haltsOnly, boolean geometry,
                                       String format, boolean includeCoordinates) {
        return record(SearchOperation.LIVE_TRAIN_STATUS, number,
                client.liveTrain(number, date, haltsOnly, geometry, format,
                        includeCoordinates));
    }

    @Override
    public RailRadarResponse route(String number, String format, boolean stops) {
        return record(SearchOperation.TRAIN_ROUTE_GEOMETRY, number,
                client.route(number, format, stops));
    }

    @Override
    public RailRadarResponse betweenStations(String from, String to, LocalDate date,
                                             boolean live, boolean byCity,
                                             String type, String category) {
        RailRadarResponse response = client.betweenStations(from, to, date, live,
                byCity, type, category);
        if (response != null && response.success()) {
            searchHistoryService.save(SearchOperation.JOURNEY_BETWEEN_STATIONS, from, to);
        }
        log.info("RailRadar {} completed for {}", SearchOperation.JOURNEY_BETWEEN_STATIONS, from);
        return response;
    }

    @Override
    public RailRadarResponse stationBoard(String code, boolean includeIntermediate) {
        RailRadarResponse response = client.stationBoard(code, includeIntermediate);
        return record(SearchOperation.STATION_BOARD, code, response);
    }

    @Override
    public RailRadarResponse stationLiveBoard(String code, int hours,
                                              boolean includeIntermediate) {
        RailRadarResponse response = client.stationLiveBoard(code, hours,
                includeIntermediate);
        return record(SearchOperation.STATION_LIVE_BOARD, code, response);
    }

    private RailRadarResponse record(SearchOperation operation, String identifier,
                                     RailRadarResponse response) {
        if (response != null && response.success()) {
            searchHistoryService.save(operation, identifier, null);
        }
        log.info("RailRadar {} completed for {}", operation, identifier);
        return response;
    }
}
