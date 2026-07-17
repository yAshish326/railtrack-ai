package com.railtrack.ai.controller;

import com.railtrack.ai.dto.AiPnrResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.ai.service.AiService;
import com.railtrack.train.dto.response.Train;
import com.railtrack.pnr.dto.response.PnrData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*") // Allows smooth connection from your React UI ports
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * Endpoint 1: Run analytical assessment on the search results.
     * Triggered right after the user clicks 'Search' on the main Dashboard view.
     */
    @PostMapping("/analyze-trains")
    public ResponseEntity<AiTrainRecommendationResponse> analyzeTrainRoutes(@RequestBody List<Train> trainList) {
        AiTrainRecommendationResponse response = aiService.generateTrainSuggestions(trainList);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2: Process current confirmation odds for an active PNR query.
     * Triggered when looking up passenger statuses on the PNR check screen.
     */
    @PostMapping("/analyze-pnr")
    public ResponseEntity<AiPnrResponse> analyzePnrStatus(@RequestBody PnrData pnrData) {
        AiPnrResponse response = aiService.analyzePnrStatus(pnrData);
        return ResponseEntity.ok(response);
    }
}