package com.railtrack.history.service.impl;

import com.railtrack.auth.entity.User;
import com.railtrack.auth.service.UserService;
import com.railtrack.history.entity.SearchHistory;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.history.repository.SearchHistoryRepository;
import com.railtrack.history.service.SearchHistoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/** Default implementation for metadata-only external search auditing with deduplication logic. */
@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private static final Logger log = LoggerFactory.getLogger(SearchHistoryServiceImpl.class);
    private final SearchHistoryRepository repository;
    private final UserService userService;

    public SearchHistoryServiceImpl(SearchHistoryRepository repository,
                                    UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void save(SearchOperation operation, String primaryIdentifier,
                     String secondaryIdentifier) {
        User user = userService.getAuthenticatedUser();

        // 🔍 1. Look for an existing identical search query history log row
        Optional<SearchHistory> existingLog = repository
                .findTopByUserAndOperationAndPrimaryIdentifierAndSecondaryIdentifierOrderBySearchedAtDesc(
                        user, operation, primaryIdentifier, secondaryIdentifier);

        if (existingLog.isPresent()) {
            LocalDateTime lastSearched = existingLog.get().getSearchedAt();
            long minutesSinceLastSearch = ChronoUnit.MINUTES.between(lastSearched, LocalDateTime.now());

            // ⏱️ 2. If it was searched less than 10 minutes ago, update the timestamp instead of inserting a duplicate row!
            if (minutesSinceLastSearch < 10) {
                SearchHistory duplicateRecord = existingLog.get();
                duplicateRecord.setSearchedAt(LocalDateTime.now());
                repository.save(duplicateRecord);
                log.info("Duplicate {} search detected within window. Updated timestamp for user: {}", operation, user.getEmail());
                return; // Exit early!
            }
        }

        // 🆕 3. Save as a brand-new search log row if outside the window
        SearchHistory history = new SearchHistory();
        history.setUser(user);
        history.setOperation(operation);
        history.setPrimaryIdentifier(primaryIdentifier);
        history.setSecondaryIdentifier(secondaryIdentifier);
        history.setSearchedAt(LocalDateTime.now());
        repository.save(history);
        log.info("{} search recorded for user {}", operation, user.getEmail());
    }
}