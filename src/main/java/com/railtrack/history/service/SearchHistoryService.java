package com.railtrack.history.service;

import com.railtrack.history.entity.SearchOperation;

/** Persists successful user-initiated search metadata. */
public interface SearchHistoryService {

    void save(SearchOperation operation, String primaryIdentifier,
              String secondaryIdentifier);
}
