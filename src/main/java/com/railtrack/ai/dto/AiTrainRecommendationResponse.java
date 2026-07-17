package com.railtrack.ai.dto;

import com.railtrack.train.dto.response.Train;

public class AiTrainRecommendationResponse {
    private String insightMessage;
    private Train fastestTrain;
    private Train longestTrain;

    public AiTrainRecommendationResponse() {
    }

    public AiTrainRecommendationResponse(String insightMessage, Train fastestTrain, Train longestTrain) {
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

    public Train getFastestTrain() {
        return fastestTrain;
    }

    public void setFastestTrain(Train fastestTrain) {
        this.fastestTrain = fastestTrain;
    }

    public Train getLongestTrain() {
        return longestTrain;
    }

    public void setLongestTrain(Train longestTrain) {
        this.longestTrain = longestTrain;
    }
}