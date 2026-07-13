package com.railtrack.station.service.impl;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import com.railtrack.station.service.StationService;
import com.railtrack.train.client.RailRadarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Coordinates station-board queries and successful-search recording. */
@Service
public class StationServiceImpl implements StationService {
    private static final Logger log = LoggerFactory.getLogger(StationServiceImpl.class);
    private final RailRadarClient client;
    private final SearchHistoryService history;

    public StationServiceImpl(RailRadarClient client, SearchHistoryService history) {
        this.client = client;
        this.history = history;
    }

    @Override
    public RailRadarResponse stationBoard(String code, boolean includeIntermediate) {
        RailRadarResponse response = client.stationBoard(code, includeIntermediate);
        return record(SearchOperation.STATION_BOARD, code, response);
    }

    @Override
    public RailRadarResponse stationLiveBoard(String code, int hours,
                                              boolean includeIntermediate) {
        RailRadarResponse response = client.stationLiveBoard(code, hours,
                includeIntermediate);
        return record(SearchOperation.STATION_LIVE_BOARD, code, response);
    }

    private RailRadarResponse record(SearchOperation operation, String code,
                                     RailRadarResponse response) {
        if (response != null && response.success()) {
            history.save(operation, code, null);
        }
        log.info("RailRadar {} completed for {}", operation, code);
        return response;
    }
}
