package com.railtrack.journey.controller;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.journey.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

/** New RailRadar journey endpoint, separate from the legacy train-search API. */
@Validated @RestController @RequestMapping("/api/v1/journey") @Tag(name = "RailRadar journeys")
public class JourneyController {
    private final JourneyService service;
    public JourneyController(JourneyService service) { this.service = service; }
    @GetMapping("/between-stations") @Operation(summary = "Find RailRadar trains between stations")
    public ResponseEntity<RailRadarResponse> betweenStations(@RequestParam @Pattern(regexp="[A-Z0-9]{1,10}", message="from must be a valid uppercase station code") String from, @RequestParam @Pattern(regexp="[A-Z0-9]{1,10}", message="to must be a valid uppercase station code") String to, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(defaultValue="false") boolean live, @RequestParam(defaultValue="false") boolean byCity, @RequestParam(required=false) String type, @RequestParam(required=false) String category) {
        return ResponseEntity.ok(service.betweenStations(from,to,date,live,byCity,type,category));
    }
}
