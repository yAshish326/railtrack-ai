package com.railtrack.train.service.impl;

import com.railtrack.auth.entity.User;
import com.railtrack.auth.service.UserService;
import com.railtrack.train.dto.request.TrainSearchHistoryRequest;
import com.railtrack.train.dto.response.TrainSearchHistoryResponse;
import com.railtrack.train.entity.TrainSearchHistory;
import com.railtrack.train.exception.TrainSearchHistoryNotFoundException;
import com.railtrack.train.repository.TrainSearchHistoryRepository;
import com.railtrack.train.service.TrainSearchHistoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** Default implementation of authenticated-user train-search history. */
@Service
public class TrainSearchHistoryServiceImpl implements TrainSearchHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(TrainSearchHistoryServiceImpl.class);

    private final TrainSearchHistoryRepository repository;
    private final UserService userService;

    public TrainSearchHistoryServiceImpl(TrainSearchHistoryRepository repository,
                                         UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    /** Persists only successful search criteria for the authenticated user. */
    @Override
    @Transactional
    public void saveSearch(TrainSearchHistoryRequest request) {
        User user = userService.getAuthenticatedUser();
        TrainSearchHistory history = new TrainSearchHistory();
        history.setUser(user);
        history.setFromStation(request.fromStation());
        history.setToStation(request.toStation());
        history.setJourneyDate(request.journeyDate());
        history.setTravelClass(request.travelClass());
        history.setQuota(request.quota());
        history.setSearchedAt(LocalDateTime.now());

        repository.save(history);
        log.info("Train search history saved for user {}", user.getEmail());
    }

    /** Returns all train-search history for the authenticated user. */
    @Override
    public List<TrainSearchHistoryResponse> getHistory() {
        User user = userService.getAuthenticatedUser();
        return repository.findByUserOrderBySearchedAtDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Deletes all train-search history belonging to the authenticated user. */
    @Override
    @Transactional
    public void deleteHistory() {
        User user = userService.getAuthenticatedUser();
        repository.deleteAll(repository.findByUserOrderBySearchedAtDesc(user));
        log.info("All train search history deleted for user {}", user.getEmail());
    }

    /** Deletes one train-search history record belonging to the authenticated user. */
    @Override
    @Transactional
    public void deleteHistoryById(Long historyId) {
        User user = userService.getAuthenticatedUser();
        TrainSearchHistory history = repository.findById(historyId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TrainSearchHistoryNotFoundException(
                        "Train search history record not found."));

        repository.delete(history);
        log.info("Train search history {} deleted for user {}", historyId,
                user.getEmail());
    }

    /** Returns the authenticated user's train-search count. */
    @Override
    public long countSearches() {
        return repository.countByUser(userService.getAuthenticatedUser());
    }

    private TrainSearchHistoryResponse toResponse(TrainSearchHistory history) {
        return new TrainSearchHistoryResponse(history.getId(),
                history.getFromStation(), history.getToStation(),
                history.getJourneyDate(), history.getTravelClass(),
                history.getQuota(), history.getSearchedAt());
    }
}
