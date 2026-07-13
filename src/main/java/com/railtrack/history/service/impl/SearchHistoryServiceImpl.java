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

/** Default implementation for metadata-only external search auditing. */
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
