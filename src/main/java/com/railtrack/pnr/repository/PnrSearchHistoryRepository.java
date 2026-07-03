package com.railtrack.pnr.repository;

import com.railtrack.pnr.entity.PnrSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PnrSearchHistoryRepository
        extends JpaRepository<PnrSearchHistory, Long> {

    Optional<PnrSearchHistory> findByPnrNumber(String pnrNumber);

}