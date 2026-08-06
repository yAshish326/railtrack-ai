package com.railtrack.ai.service;

import com.railtrack.ai.dto.AiPnrResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.train.dto.response.TrainSummaryResponse;
import com.railtrack.pnr.dto.response.PnrData;

import java.util.List;

public interface AiService {
    AiTrainRecommendationResponse generateTrainSuggestions(List<TrainSummaryResponse> trains);
    AiPnrResponse analyzePnrStatus(PnrData pnrData);
}
