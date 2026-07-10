package com.railtrack.pnr.service.impl;

import com.railtrack.pnr.client.RailwayApiClient;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.entity.PnrSearchHistory;
import com.railtrack.pnr.repository.PnrSearchHistoryRepository;
import com.railtrack.pnr.service.PnrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PnrServiceImpl implements PnrService {

    private static final Logger log =
            LoggerFactory.getLogger(PnrServiceImpl.class);

    private final RailwayApiClient railwayApiClient;
    private final PnrSearchHistoryRepository repository;

    public PnrServiceImpl(RailwayApiClient railwayApiClient,
                          PnrSearchHistoryRepository repository) {

        this.railwayApiClient = railwayApiClient;
        this.repository = repository;
    }

    private void mapResponseToEntity(PnrSearchHistory history,
                                     PnrResponse response) {

        history.setTrainNumber(response.getData().getTrainNumber());
        history.setTrainName(response.getData().getTrainName());
        history.setSourceStation(response.getData().getSourceStation());
        history.setDestinationStation(response.getData().getDestinationStation());
        history.setJourneyClass(response.getData().getJourneyClass());
        history.setChartStatus(response.getData().getChartStatus());
        history.setSearchedAt(LocalDateTime.now());
    }

    @Override
    public PnrResponse getPnrStatus(String pnrNumber) {

        log.info("Searching PNR: {}", pnrNumber);
        log.info("Calling Railway API...");
        PnrResponse response = railwayApiClient.getPnrStatus(pnrNumber);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("PNR information is not available.");
        }
        log.info("Railway API response received successfully.");

        Optional<PnrSearchHistory> existingRecord = repository.findByPnrNumber(pnrNumber);

        if (existingRecord.isPresent()) {
            PnrSearchHistory history = existingRecord.get();
            mapResponseToEntity(history, response);
            repository.save(history);
            log.info("Existing PNR {} updated successfully.", pnrNumber);
        } else {
            PnrSearchHistory history = new PnrSearchHistory();
            history.setPnrNumber(response.getData().getPnrNumber());
            mapResponseToEntity(history, response);
            repository.save(history);
            log.info("New PNR {} inserted successfully.", pnrNumber);
        }
        log.info("PNR search completed successfully for {}", pnrNumber);
        return response;
    }
}