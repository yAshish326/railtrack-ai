package com.railtrack.train.controller;

import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
            String to,

            @RequestParam
            @NotNull(message = "Journey date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate journeyDate,

            @RequestParam
            @NotBlank(message = "Travel class is required")
            String travelClass,

            @RequestParam
            @NotBlank(message = "Quota is required")
            String quota) {
        TrainSearchResponse response =
                trainService.searchTrains(from, to, journeyDate, travelClass,
                        quota);
        return ResponseEntity.ok(response);
    }
}