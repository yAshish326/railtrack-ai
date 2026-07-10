package com.railtrack.train.service.impl;

import com.railtrack.train.client.TrainApiClient;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainService;
import com.railtrack.train.util.TrainRecommendationUtil;
import org.springframework.stereotype.Service;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainApiClient trainApiClient;
    private final TrainRecommendationUtil recommendationUtil;

    public TrainServiceImpl(
            TrainApiClient trainApiClient,
            TrainRecommendationUtil recommendationUtil) {

        this.trainApiClient = trainApiClient;
        this.recommendationUtil = recommendationUtil;
    }

    @Override
    public TrainSearchResponse searchTrains(String from, String to) {

        return trainApiClient.searchTrains(from, to);
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