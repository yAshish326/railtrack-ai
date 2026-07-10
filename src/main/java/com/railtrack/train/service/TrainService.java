package com.railtrack.train.service;

import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.TrainSearchResponse;

public interface TrainService {

    // Existing search API
    TrainSearchResponse searchTrains(String from, String to);

    // AI recommendation API
    RecommendedTrainResponse getRecommendedTrain(String from, String to);

}