package com.railtrack.train.service.impl;

import com.railtrack.train.client.TrainApiClient;
import com.railtrack.train.dto.request.TrainSearchHistoryRequest;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainService;
import com.railtrack.train.service.TrainSearchHistoryService;
import com.railtrack.train.util.TrainRecommendationUtil;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainApiClient trainApiClient;
    private final TrainRecommendationUtil recommendationUtil;
    private final TrainSearchHistoryService trainSearchHistoryService;
    private final SearchHistoryService searchHistoryService;

    public TrainServiceImpl(
            TrainApiClient trainApiClient,
            TrainRecommendationUtil recommendationUtil,
            TrainSearchHistoryService trainSearchHistoryService,
            SearchHistoryService searchHistoryService) {

        this.trainApiClient = trainApiClient;
        this.recommendationUtil = recommendationUtil;
        this.trainSearchHistoryService = trainSearchHistoryService;
        this.searchHistoryService = searchHistoryService;
    }

    @Override
    public TrainSearchResponse searchTrains(String from, String to) {

        return trainApiClient.searchTrains(from, to);
    }

    /**
     * Executes the existing external search, then records only the submitted
     * criteria after a successful response has been returned by the client.
     */
    @Override
    public TrainSearchResponse searchTrains(String from, String to,
                                            LocalDate journeyDate,
                                            String travelClass, String quota) {
        TrainSearchResponse response = trainApiClient.searchTrains(from, to);
        if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
            trainSearchHistoryService.saveSearch(new TrainSearchHistoryRequest(
                    from, to, journeyDate, travelClass, quota));
            searchHistoryService.save(SearchOperation.TRAIN_SEARCH, from, to);
        }
        return response;
    }

    @Override

    public RecommendedTrainResponse getRecommendedTrain(String from, String to) {

        TrainSearchResponse response =
                trainApiClient.searchTrains(from, to);

        Train bestTrain = recommendationUtil.getBestTrain(
                response.getData().getTrains());

        return new RecommendedTrainResponse(
                bestTrain,
                response
        );
    }
}
