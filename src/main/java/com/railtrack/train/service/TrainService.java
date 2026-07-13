package com.railtrack.train.service;

import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.TrainSearchResponse;

import java.time.LocalDate;

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

}
