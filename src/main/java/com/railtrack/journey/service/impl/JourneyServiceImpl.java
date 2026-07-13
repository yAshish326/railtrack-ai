package com.railtrack.journey.service.impl;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import com.railtrack.journey.service.JourneyService;
import com.railtrack.train.client.RailRadarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Coordinates journey queries and successful-search recording. */
@Service
public class JourneyServiceImpl implements JourneyService {
    private static final Logger log = LoggerFactory.getLogger(JourneyServiceImpl.class);
    private final RailRadarClient client;
    private final SearchHistoryService history;

    public JourneyServiceImpl(RailRadarClient client, SearchHistoryService history) {
        this.client = client;
        this.history = history;
    }

    @Override
    public RailRadarResponse betweenStations(String from, String to, LocalDate date,
                                             boolean live, boolean byCity,
                                             String type, String category) {
        RailRadarResponse response = client.betweenStations(from, to, date, live,
                byCity, type, category);
        if (response != null && response.success()) {
            history.save(SearchOperation.JOURNEY_BETWEEN_STATIONS, from, to);
        }
        log.info("RailRadar {} completed for {}", SearchOperation.JOURNEY_BETWEEN_STATIONS,
                from);
        return response;
    }
}
