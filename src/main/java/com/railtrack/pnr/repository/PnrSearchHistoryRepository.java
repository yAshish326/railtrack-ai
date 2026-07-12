package com.railtrack.pnr.repository;

import com.railtrack.auth.entity.User;
import com.railtrack.pnr.entity.PnrSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PnrSearchHistoryRepository
        extends JpaRepository<PnrSearchHistory, Long> {

    Optional<PnrSearchHistory> findByPnrNumber(String pnrNumber);

    Optional<PnrSearchHistory> findByUserAndPnrNumber(User user,
                                                       String pnrNumber);

    List<PnrSearchHistory> findByUserOrderBySearchedAtDesc(User user);

    long countByUser(User user);

    List<PnrSearchHistory> findTop5ByUserOrderBySearchedAtDesc(User user);

    Optional<PnrSearchHistory> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);

}
