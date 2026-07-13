package com.railtrack.train.dto.request;

import java.time.LocalDate;

/** Search criteria persisted after a successful train search. */
public record TrainSearchHistoryRequest(
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        String travelClass,
        String quota) {
}
