package com.railtrack.ai.dto;

import com.railtrack.ai.dto.response.AiRecommendationSummary;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.dto.response.TrainSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiTrainRecommendationResponse {

    private Train recommendedTrain;
    private AiRecommendationSummary summary;
    private TrainSearchResponse allTrains;
    private LocalDateTime generatedAt;
    private String model;
}