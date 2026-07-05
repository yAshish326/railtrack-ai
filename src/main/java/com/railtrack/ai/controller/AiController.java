package com.railtrack.ai.controller;

import com.railtrack.ai.dto.AiChatRequest;
import com.railtrack.ai.dto.AiChatResponse;
import com.railtrack.ai.service.AiService;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.service.PnrService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Constructor Injection
    public AiController(AiService aiService, PnrService pnrService) {
        this.aiService = aiService;
        this.pnrService = pnrService;
    }
    private final PnrService pnrService;
    /*
     * =========================================================================
     * AI Chat Endpoint
     * Sends the user's message to Gemini and returns the AI response.
     * =========================================================================
     */
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @Valid @RequestBody AiChatRequest request) {

        AiChatResponse response =
                aiService.chat(request.getMessage());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/pnr/{pnrNumber}")
    public ResponseEntity<AiChatResponse> explainPnr(
            @PathVariable
            @Pattern(regexp = "\\d{10}",
                    message = "PNR number must contain exactly 10 digits")
            String pnrNumber) {

        PnrResponse response = pnrService.getPnrStatus(pnrNumber);

        String explanation = aiService.explainPnr(response);

        return ResponseEntity.ok(
                new AiChatResponse(explanation)
        );
    }
}