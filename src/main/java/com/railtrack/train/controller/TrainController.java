package com.railtrack.train.controller;

import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/train")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/between-stations")
    public ResponseEntity<TrainSearchResponse> searchTrains(

            @RequestParam
            @NotBlank(message = "Source station is required")
            String from,

            @RequestParam
            @NotBlank(message = "Destination station is required")
            String to) {

        TrainSearchResponse response =
                trainService.searchTrains(from, to);

        return ResponseEntity.ok(response);
    }
}