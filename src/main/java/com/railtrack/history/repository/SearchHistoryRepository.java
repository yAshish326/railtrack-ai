package com.railtrack.history.repository;

import com.railtrack.history.entity.SearchHistory;
import com.railtrack.history.entity.SearchOperation;
import com.railtrack.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/** Persistence gateway for metadata-only search history. */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    void deleteByUser(User user);

    // ✅ Finds the most recent search signature matching this exact route for this user
    Optional<SearchHistory> findTopByUserAndOperationAndPrimaryIdentifierAndSecondaryIdentifierOrderBySearchedAtDesc(
            User user, SearchOperation operation, String primaryIdentifier, String secondaryIdentifier);
}