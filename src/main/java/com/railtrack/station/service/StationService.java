package com.railtrack.station.service;

import com.railtrack.common.dto.RailRadarResponse;

/** Domain service for static and live station boards. */
public interface StationService {
    RailRadarResponse stationBoard(String code, boolean includeIntermediate);
    RailRadarResponse stationLiveBoard(String code, int hours,
                                       boolean includeIntermediate);
}
