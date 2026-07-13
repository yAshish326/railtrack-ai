package com.railtrack.history.entity;

/** Identifies the user-initiated external lookup that was performed. */
public enum SearchOperation {
    PNR_SEARCH,
    TRAIN_SEARCH,
    TRAIN_DETAILS,
    LIVE_TRAIN_STATUS,
    TRAIN_ROUTE_GEOMETRY,
    JOURNEY_BETWEEN_STATIONS,
    STATION_BOARD,
    STATION_LIVE_BOARD
}
