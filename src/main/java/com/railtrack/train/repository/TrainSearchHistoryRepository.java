package com.railtrack.train.repository;

import com.railtrack.auth.entity.User;
import com.railtrack.train.entity.TrainSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository for user-owned train-search criteria. */
@Repository
public interface TrainSearchHistoryRepository
        extends JpaRepository<TrainSearchHistory, Long> {

    long countByUser(User user);

    List<TrainSearchHistory> findTop5ByUserOrderBySearchedAtDesc(User user);

    List<TrainSearchHistory> findByUserOrderBySearchedAtDesc(User user);
}
