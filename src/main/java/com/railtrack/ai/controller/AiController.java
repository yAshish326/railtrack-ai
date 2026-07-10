package com.railtrack.ai.controller;

import com.railtrack.ai.dto.AiChatRequest;
import com.railtrack.ai.dto.AiChatResponse;
import com.railtrack.ai.dto.AiPnrResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.ai.service.AiService;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.service.PnrService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/*
 * ============================================================================
 * AiController
 * ----------------------------------------------------------------------------
 * Handles all AI-related REST APIs.
 * ============================================================================
 */

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;
    private final PnrService pnrService;

    // Constructor Injection
    public AiController(AiService aiService,
                        PnrService pnrService) {
        this.aiService = aiService;
        this.pnrService = pnrService;
    }

    /*
     * =========================================================================
     * AI Chat Endpoint
     * =========================================================================
     */
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @Valid @RequestBody AiChatRequest request) {

        AiChatResponse response =
                aiService.chat(request.getMessage());

        return ResponseEntity.ok(response);
    }

    /*
     * =========================================================================
     * AI PNR Explanation Endpoint
     * =========================================================================
     */
    @GetMapping("/pnr/{pnrNumber}")
    public ResponseEntity<AiPnrResponse> explainPnr(
            @PathVariable
            @Pattern(regexp = "\\d{10}",
                    message = "PNR number must contain exactly 10 digits")
            String pnrNumber) {

        PnrResponse pnrResponse = pnrService.getPnrStatus(pnrNumber);

        String explanation = aiService.explainPnr(pnrResponse);

        AiPnrResponse response = new AiPnrResponse(
                pnrResponse,
                explanation,
                LocalDateTime.now(),
                "Gemini 2.5 Flash"
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/train-recommendation")
    public ResponseEntity<AiTrainRecommendationResponse> recommendTrain(
            @RequestParam String from,
            @RequestParam String to) {

        return ResponseEntity.ok(
                aiService.recommendTrain(from, to)
        );
    }
}