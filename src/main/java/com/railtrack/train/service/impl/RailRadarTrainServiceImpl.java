package com.railtrack.train.service.impl;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import com.railtrack.train.client.RailRadarClient;
import com.railtrack.train.service.RailRadarTrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Coordinates train-domain RailRadar calls and successful-search recording. */
@Service
public class RailRadarTrainServiceImpl implements RailRadarTrainService {
    private static final Logger log = LoggerFactory.getLogger(RailRadarTrainServiceImpl.class);
    private final RailRadarClient client;
    private final SearchHistoryService history;

    public RailRadarTrainServiceImpl(RailRadarClient client,
                                     SearchHistoryService history) {
        this.client = client;
        this.history = history;
    }

    @Override
    public RailRadarResponse trainDetails(String number, LocalDate journeyDate,
                                          String dataType, String dataProvider,
                                          String userId) {
        return record(SearchOperation.TRAIN_DETAILS, number,
                client.trainDetails(number, journeyDate, dataType, dataProvider, userId));
    }

    @Override
    public RailRadarResponse liveTrain(String number, LocalDate date,
                                       boolean haltsOnly, boolean geometry,
                                       String format, boolean includeCoordinates) {
        return record(SearchOperation.LIVE_TRAIN_STATUS, number,
                client.liveTrain(number, date, haltsOnly, geometry, format,
                        includeCoordinates));
    }

    @Override
    public RailRadarResponse route(String number, String format, boolean stops) {
        return record(SearchOperation.TRAIN_ROUTE_GEOMETRY, number,
                client.route(number, format, stops));
    }

    private RailRadarResponse record(SearchOperation operation, String identifier,
                                     RailRadarResponse response) {
        if (response != null && response.success()) {
            history.save(operation, identifier, null);
        }
        log.info("RailRadar {} completed for {}", operation, identifier);
        return response;
    }
}
