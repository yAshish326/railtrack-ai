package com.railtrack.train.controller;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.train.dto.response.TrainSearchResponse;
import com.railtrack.train.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/v1/train")
@Tag(name = "Train APIs")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @Operation(summary = "Search trains between stations")
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

        return ResponseEntity.ok(
                trainService.searchTrains(
                        from,
                        to,
                        journeyDate,
                        travelClass,
                        quota));
    }

    @Operation(summary = "Train Details")
    @GetMapping("/details/{trainNumber}")
    public ResponseEntity<RailRadarResponse> trainDetails(

            @PathVariable String trainNumber,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate journeyDate,

            @RequestParam(required = false)
            String dataType,

            @RequestParam(required = false)
            String dataProvider,

            @RequestParam(required = false)
            String userId) {

        return ResponseEntity.ok(
                trainService.trainDetails(
                        trainNumber,
                        journeyDate,
                        dataType,
                        dataProvider,
                        userId));
    }

    @Operation(summary = "Live Train Status")
    @GetMapping("/live/{trainNumber}")
    public ResponseEntity<RailRadarResponse> liveTrain(

            @PathVariable String trainNumber,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "false")
            boolean haltsOnly,

            @RequestParam(defaultValue = "false")
            boolean geometry,

            @RequestParam(defaultValue = "polyline")
            String format,

            @RequestParam(defaultValue = "false")
            boolean includeCoordinates) {

        return ResponseEntity.ok(
                trainService.liveTrain(
                        trainNumber,
                        date,
                        haltsOnly,
                        geometry,
                        format,
                        includeCoordinates));
    }

    @Operation(summary = "Train Route Geometry")
    @GetMapping("/route/{trainNumber}")
    public ResponseEntity<RailRadarResponse> route(

            @PathVariable String trainNumber,

            @RequestParam(defaultValue = "polyline")
            String format,

            @RequestParam(defaultValue = "true")
            boolean stops) {

        return ResponseEntity.ok(
                trainService.route(
                        trainNumber,
                        format,
                        stops));
    }

    @Operation(summary = "Station Board")
    @GetMapping("/station-board/{stationCode}")
    public ResponseEntity<RailRadarResponse> stationBoard(

            @PathVariable
            @Pattern(regexp = "[A-Z0-9]{1,10}", message = "Station code must contain 1 to 10 uppercase letters or digits")
            String stationCode,

            @RequestParam(defaultValue = "false")
            boolean includeIntermediate) {

        return ResponseEntity.ok(
                trainService.stationBoard(
                        stationCode,
                        includeIntermediate));
    }

    @Operation(summary = "Live Station Board")
    @GetMapping("/live-board/{stationCode}")
    public ResponseEntity<RailRadarResponse> liveBoard(

            @PathVariable
            @Pattern(regexp = "[A-Z0-9]{1,10}", message = "Station code must contain 1 to 10 uppercase letters or digits")
            String stationCode,

            @RequestParam(defaultValue = "4")
            @Pattern(regexp = "2|4|6|8", message = "hours must be 2, 4, 6, or 8")
            String hours,

            @RequestParam(defaultValue = "false")
            boolean includeIntermediate) {

        return ResponseEntity.ok(
                trainService.stationLiveBoard(
                        stationCode,
                        Integer.parseInt(hours),
                        includeIntermediate));
    }
}