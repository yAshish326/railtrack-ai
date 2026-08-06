package com.railtrack.ai.dto;

import com.railtrack.train.dto.response.TrainSummaryResponse;

public class AiTrainRecommendationResponse {
    private String insightMessage;
    private TrainSummaryResponse fastestTrain;
    private TrainSummaryResponse longestTrain;

    public AiTrainRecommendationResponse() {
    }

    public AiTrainRecommendationResponse(String insightMessage, TrainSummaryResponse fastestTrain, TrainSummaryResponse longestTrain) {
        this.insightMessage = insightMessage;
        this.fastestTrain = fastestTrain;
        this.longestTrain = longestTrain;
    }

    // Getters and Setters
    public String getInsightMessage() {
        return insightMessage;
    }

    public void setInsightMessage(String insightMessage) {
        this.insightMessage = insightMessage;
    }

    public TrainSummaryResponse getFastestTrain() {
        return fastestTrain;
    }

    public void setFastestTrain(TrainSummaryResponse fastestTrain) {
        this.fastestTrain = fastestTrain;
    }

    public TrainSummaryResponse getLongestTrain() {
        return longestTrain;
    }

    public void setLongestTrain(TrainSummaryResponse longestTrain) {
        this.longestTrain = longestTrain;
    }
}
