package com.railtrack.station.controller;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.station.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** RailRadar station board endpoints. */
@Validated @RestController @RequestMapping("/api/v1/station") @Tag(name = "RailRadar stations")
public class StationController {
    private final StationService service;
    public StationController(StationService service) {
        this.service = service; }
    @GetMapping("/board/{stationCode}")
    @Operation(summary = "Get the static RailRadar station board")
    public ResponseEntity<RailRadarResponse> board(@PathVariable @Pattern(regexp="[A-Z0-9]{1,10}", message="Station code must contain 1 to 10 uppercase letters or digits") String stationCode, @RequestParam(defaultValue="false") boolean includeIntermediate) { return ResponseEntity.ok(service.stationBoard(stationCode,includeIntermediate)); }
    @GetMapping("/live-board/{stationCode}") @Operation(summary = "Get the live RailRadar station board")
    public ResponseEntity<RailRadarResponse> liveBoard(@PathVariable @Pattern(regexp="[A-Z0-9]{1,10}", message="Station code must contain 1 to 10 uppercase letters or digits") String stationCode, @RequestParam(defaultValue="4") @Pattern(regexp="2|4|6|8", message="hours must be 2, 4, 6, or 8") String hours, @RequestParam(defaultValue="false") boolean includeIntermediate) { return ResponseEntity.ok(service.stationLiveBoard(stationCode,Integer.parseInt(hours),includeIntermediate)); }
}
