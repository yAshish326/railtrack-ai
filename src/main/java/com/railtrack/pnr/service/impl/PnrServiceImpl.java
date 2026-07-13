package com.railtrack.pnr.service.impl;

import com.railtrack.auth.entity.User;
import com.railtrack.auth.service.UserService;
import com.railtrack.pnr.client.RailwayApiClient;
import com.railtrack.pnr.dto.response.PnrHistoryResponse;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.entity.PnrSearchHistory;
import com.railtrack.pnr.exception.PnrHistoryNotFoundException;
import com.railtrack.pnr.repository.PnrSearchHistoryRepository;
import com.railtrack.pnr.service.PnrService;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.service.SearchHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PnrServiceImpl implements PnrService {

    private static final Logger log =
            LoggerFactory.getLogger(PnrServiceImpl.class);

    private final RailwayApiClient railwayApiClient;
    private final PnrSearchHistoryRepository repository;
    private final UserService userService;
    private final SearchHistoryService searchHistoryService;

    public PnrServiceImpl(RailwayApiClient railwayApiClient,
                          PnrSearchHistoryRepository repository,
                          UserService userService,
                          SearchHistoryService searchHistoryService) {

        this.railwayApiClient = railwayApiClient;
        this.repository = repository;
        this.userService = userService;
        this.searchHistoryService = searchHistoryService;
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

    private PnrHistoryResponse mapHistoryToResponse(
            PnrSearchHistory history) {

        return new PnrHistoryResponse(
                history.getId(),
                history.getPnrNumber(),
                history.getTrainNumber(),
                history.getTrainName(),
                history.getSourceStation(),
                history.getDestinationStation(),
                history.getJourneyClass(),
                history.getChartStatus(),
                history.getSearchedAt()
        );
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

        User currentUser = userService.getAuthenticatedUser();
        Optional<PnrSearchHistory> existingRecord =
                repository.findByUserAndPnrNumber(currentUser, pnrNumber);

        if (existingRecord.isPresent()) {
            PnrSearchHistory history = existingRecord.get();
            mapResponseToEntity(history, response);
            repository.save(history);
            log.info("Existing PNR {} updated successfully.", pnrNumber);
        } else {
            PnrSearchHistory history = new PnrSearchHistory();
            history.setUser(currentUser);
            history.setPnrNumber(response.getData().getPnrNumber());
            mapResponseToEntity(history, response);
            repository.save(history);
            log.info("New PNR {} inserted successfully.", pnrNumber);
        }
        log.info("PNR search completed successfully for {}", pnrNumber);
        searchHistoryService.save(SearchOperation.PNR_SEARCH, pnrNumber, null);
        return response;
    }

    @Override
    public List<PnrHistoryResponse> getPnrHistory() {

        User currentUser = userService.getAuthenticatedUser();

        return repository.findByUserOrderBySearchedAtDesc(currentUser)
                .stream()
                .map(this::mapHistoryToResponse)
                .toList();
    }

    /**
     * Deletes one PNR history record for the authenticated user.
     */
    @Override
    @Transactional
    public void deletePnrHistory(Long historyId) {
        User currentUser = userService.getAuthenticatedUser();

        PnrSearchHistory history = repository.findByIdAndUser(
                        historyId, currentUser)
                .orElseThrow(() -> new PnrHistoryNotFoundException(
                        "PNR history record not found."
                ));

        repository.delete(history);
        log.info("PNR history {} deleted for user {}", historyId,
                currentUser.getEmail());
    }

    /**
     * Deletes all PNR history records for the authenticated user.
     */
    @Override
    @Transactional
    public void deleteAllPnrHistory() {
        User currentUser = userService.getAuthenticatedUser();

        repository.deleteByUser(currentUser);
        log.info("All PNR history deleted for user {}",
                currentUser.getEmail());
    }
}
