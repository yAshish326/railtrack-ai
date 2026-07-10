package com.railtrack.ai.controller;

import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.ai.service.AiHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Handles AI history APIs for the logged-in user.
 */
@RestController
@RequestMapping("/api/v1/ai/history")
public class AiHistoryController {

    private final AiHistoryService aiHistoryService;

    public AiHistoryController(AiHistoryService aiHistoryService) {
        this.aiHistoryService = aiHistoryService;
    }

    /**
     * Returns AI history for the authenticated user.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<List<AiHistoryResponse>> getCurrentUserHistory() {
        return ResponseEntity.ok(aiHistoryService.getCurrentUserHistory());
    }

    /**
     * Deletes one AI history record for the authenticated user.
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Map<String, String>> deleteCurrentUserHistory(
            @PathVariable Long historyId) {

        aiHistoryService.deleteCurrentUserHistory(historyId);

        return ResponseEntity.ok(
                Map.of("message", "AI history deleted successfully.")
        );
    }

    /**
     * Deletes all AI history records for the authenticated user.
     */
    @DeleteMapping({"", "/"})
    public ResponseEntity<Map<String, String>> deleteCurrentUserHistory() {
        aiHistoryService.deleteCurrentUserHistory();

        return ResponseEntity.ok(
                Map.of("message", "AI history deleted successfully.")
        );
    }
}
