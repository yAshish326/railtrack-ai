package com.railtrack.train.service;

import com.railtrack.common.dto.RailRadarResponse;

import java.time.LocalDate;

/** Domain service for RailRadar train detail, live-status, and route queries. */
public interface RailRadarTrainService {
    RailRadarResponse trainDetails(String number, LocalDate journeyDate,
                                   String dataType, String dataProvider,
                                   String userId);
    RailRadarResponse liveTrain(String number, LocalDate date, boolean haltsOnly,
                                boolean geometry, String format,
                                boolean includeCoordinates);
    RailRadarResponse route(String number, String format, boolean stops);
}
