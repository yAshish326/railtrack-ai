package com.railtrack.train.controller;

import com.railtrack.train.dto.response.JourneyResponse;
import com.railtrack.train.dto.response.LiveStationBoardResponse;
import com.railtrack.train.dto.response.LiveTrainResponse;
import com.railtrack.train.dto.response.StationBoardResponse;
import com.railtrack.train.dto.response.TrainDetailsResponse;
import com.railtrack.train.dto.response.TrainRouteResponse;
import com.railtrack.train.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * All train-search, detail, live-status, route, and station-board
 * capabilities live here as a single endpoint per capability (no
 * duplicate/provider-specific routes - see the architecture review notes).
 * Every response returned is a business DTO produced by {@link
 * com.railtrack.train.mapper.RailRadarMapper}; raw RailRadar JSON never
 * reaches the frontend.
 */
@Validated
@RestController
@RequestMapping("/api/v1/train")
@Tag(name = "Train APIs", description = "Train search, details, live status, route, and station board")
public class TrainController {

    private static final String STATION_CODE_PATTERN = "[A-Z0-9]{1,10}";
    private static final String STATION_CODE_MESSAGE =
            "must be 1-10 uppercase letters or digits";

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @Operation(
            summary = "Search trains between stations",
            description = "Searches for trains running between a source and destination station "
                    + "via RailRadar. Optionally filter by journey date, travel class, and quota. "
                    + "This is the single canonical search endpoint - the legacy provider is no "
                    + "longer exposed here.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Journey search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid station code or request parameters"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error"),
            @ApiResponse(responseCode = "504", description = "RailRadar did not respond within the configured timeout")
    })
    @GetMapping("/between-stations")
    public ResponseEntity<JourneyResponse> betweenStations(

            @RequestParam
            @Pattern(regexp = STATION_CODE_PATTERN, message = "from " + STATION_CODE_MESSAGE)
            String from,

            @RequestParam
            @Pattern(regexp = STATION_CODE_PATTERN, message = "to " + STATION_CODE_MESSAGE)
            String to,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "false")
            boolean live,

            @RequestParam(defaultValue = "false")
            boolean byCity,

            @RequestParam(required = false)
            String type,

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            String quota,

            @RequestParam(required = false)
            String travelClass) {

        return ResponseEntity.ok(
                trainService.betweenStations(from, to, date, live, byCity, type, category, quota, travelClass));
    }

    @Operation(
            summary = "Train Details",
            description = "Returns static details for a train: name, type, source/destination "
                    + "stations, distance, travel time, and running days.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Train details returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid train number or request parameters"),
            @ApiResponse(responseCode = "404", description = "Train not found"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error")
    })
    @GetMapping("/details/{trainNumber}")
    public ResponseEntity<TrainDetailsResponse> trainDetails(

            @PathVariable
            @Pattern(regexp = "[0-9]{5}", message = "trainNumber must be a 5-digit train number")
            String trainNumber,

            @RequestParam(defaultValue = "false")
            boolean haltsOnly) {

        return ResponseEntity.ok(
                trainService.trainDetails(trainNumber, haltsOnly));
    }

    @Operation(
            summary = "Live Train Status",
            description = "Returns the current live position and running status of a train: "
                    + "current/previous/next station, coordinates, delay, platform, and speed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Live status returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid train number or request parameters"),
            @ApiResponse(responseCode = "404", description = "Train not found or not currently running"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error")
    })
    @GetMapping("/live/{trainNumber}")
    public ResponseEntity<LiveTrainResponse> liveTrain(

            @PathVariable
            @Pattern(regexp = "[0-9]{5}", message = "trainNumber must be a 5-digit train number")
            String trainNumber,

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

    @Operation(
            summary = "Train Route Geometry",
            description = "Returns the complete stop-by-stop route for a train: station code/name, "
                    + "arrival/departure, platform, coordinates, distance, and day number for each stop.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid train number or request parameters"),
            @ApiResponse(responseCode = "404", description = "Train not found"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error")
    })
    @GetMapping("/route/{trainNumber}")
    public ResponseEntity<TrainRouteResponse> route(

            @PathVariable
            @Pattern(regexp = "[0-9]{5}", message = "trainNumber must be a 5-digit train number")
            String trainNumber,

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

    @Operation(
            summary = "Station Board",
            description = "Returns the scheduled train board for a station: arrivals/departures, "
                    + "delay, platform, and status for each train.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Station board returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid station code"),
            @ApiResponse(responseCode = "404", description = "Station not found"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error")
    })
    @GetMapping("/station-board/{stationCode}")
    public ResponseEntity<StationBoardResponse> stationBoard(

            @PathVariable
            @Pattern(regexp = STATION_CODE_PATTERN, message = "stationCode " + STATION_CODE_MESSAGE)
            String stationCode,

            @RequestParam(defaultValue = "false")
            boolean includeIntermediate) {

        return ResponseEntity.ok(
                trainService.stationBoard(
                        stationCode,
                        includeIntermediate));
    }

    @Operation(
            summary = "Live Station Board",
            description = "Returns the live train board for a station, grouped into arriving, "
                    + "departing, delayed, and cancelled trains.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Live station board returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid station code or hours window"),
            @ApiResponse(responseCode = "404", description = "Station not found"),
            @ApiResponse(responseCode = "502", description = "RailRadar returned an error")
    })
    @GetMapping("/live-board/{stationCode}")
    public ResponseEntity<LiveStationBoardResponse> liveBoard(

            @PathVariable
            @Pattern(regexp = STATION_CODE_PATTERN, message = "stationCode " + STATION_CODE_MESSAGE)
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
