package com.railtrack.journey.service;

import com.railtrack.common.dto.RailRadarResponse;

import java.time.LocalDate;

/** Domain service for journeys between two stations. */
public interface JourneyService {
    RailRadarResponse betweenStations(String from, String to, LocalDate date,
                                      boolean live, boolean byCity, String type,
                                      String category);
}
