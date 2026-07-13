package com.railtrack.train.controller;

import com.railtrack.train.dto.response.TrainSearchHistoryMessageResponse;
import com.railtrack.train.dto.response.TrainSearchHistoryResponse;
import com.railtrack.train.service.TrainSearchHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Handles authenticated-user train-search history APIs. */
@RestController
@RequestMapping("/api/v1/trains/history")
public class TrainSearchHistoryController {

    private final TrainSearchHistoryService trainSearchHistoryService;

    public TrainSearchHistoryController(
            TrainSearchHistoryService trainSearchHistoryService) {
        this.trainSearchHistoryService = trainSearchHistoryService;
    }

    /** Returns all train searches for the authenticated user. */
    @GetMapping
    public ResponseEntity<List<TrainSearchHistoryResponse>> getHistory() {
        return ResponseEntity.ok(trainSearchHistoryService.getHistory());
    }

    /** Deletes one train-search history entry owned by the authenticated user. */
    @DeleteMapping("/{id}")
    public ResponseEntity<TrainSearchHistoryMessageResponse> deleteHistoryById(
            @PathVariable Long id) {
        trainSearchHistoryService.deleteHistoryById(id);
        return ResponseEntity.ok(new TrainSearchHistoryMessageResponse(
                "Train search history deleted successfully."));
    }

    /** Deletes all train-search history entries owned by the authenticated user. */
    @DeleteMapping
    public ResponseEntity<TrainSearchHistoryMessageResponse> deleteHistory() {
        trainSearchHistoryService.deleteHistory();
        return ResponseEntity.ok(new TrainSearchHistoryMessageResponse(
                "All train search history deleted successfully."));
    }
}
