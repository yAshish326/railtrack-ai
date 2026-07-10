package com.railtrack.ai.repository;

import com.railtrack.ai.entity.AiHistory;
import com.railtrack.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistory, Long> {

    List<AiHistory> findByUserOrderByCreatedAtDesc(User user);

    Optional<AiHistory> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}
