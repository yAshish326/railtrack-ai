package com.railtrack.history.repository;

import com.railtrack.history.entity.SearchHistory;
import com.railtrack.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Persistence gateway for metadata-only search history. */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    void deleteByUser(User user);
}
