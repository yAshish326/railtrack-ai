package com.railtrack.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Lightweight response DTO for a recent train search on the dashboard. */
public record DashboardTrainSearchResponse(
        Long id,
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        String travelClass,
        String quota,
        LocalDateTime searchedAt) {
}
