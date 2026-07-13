package com.railtrack.train.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO for a persisted train search. */
public record TrainSearchHistoryResponse(
        Long id,
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        String travelClass,
        String quota,
        LocalDateTime searchedAt) {
}
