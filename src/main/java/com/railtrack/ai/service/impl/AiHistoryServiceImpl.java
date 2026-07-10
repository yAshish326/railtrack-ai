package com.railtrack.ai.service.impl;

import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.ai.entity.AiHistory;
import com.railtrack.ai.exception.AiHistoryNotFoundException;
import com.railtrack.ai.repository.AiHistoryRepository;
import com.railtrack.ai.service.AiHistoryService;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiHistoryServiceImpl implements AiHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(AiHistoryServiceImpl.class);

    private final AiHistoryRepository aiHistoryRepository;
    private final UserService userService;

    public AiHistoryServiceImpl(AiHistoryRepository aiHistoryRepository,
                                UserService userService) {
        this.aiHistoryRepository = aiHistoryRepository;
        this.userService = userService;
    }

    /**
     * Saves a successful AI response for the supplied user.
     */
    @Override
    @Transactional
    public void saveHistory(User user,
                            String pnrNumber,
                            String prompt,
                            String aiResponse) {

        AiHistory history = new AiHistory();
        history.setUser(user);
        history.setPnrNumber(pnrNumber);
        history.setPrompt(prompt);
        history.setAiResponse(aiResponse);

        aiHistoryRepository.save(history);
        log.info("AI history saved for user {} and PNR {}",
                user.getEmail(), pnrNumber);
    }

    /**
     * Returns AI history for the currently authenticated user.
     */
    @Override
    public List<AiHistoryResponse> getCurrentUserHistory() {
        User user = userService.getAuthenticatedUser();

        return aiHistoryRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Deletes one AI history record belonging to the current user.
     */
    @Override
    @Transactional
    public void deleteCurrentUserHistory(Long historyId) {
        User user = userService.getAuthenticatedUser();

        AiHistory history = aiHistoryRepository.findByIdAndUser(historyId, user)
                .orElseThrow(() -> new AiHistoryNotFoundException(
                        "AI history record not found."
                ));

        aiHistoryRepository.delete(history);
        log.info("AI history {} deleted for user {}", historyId, user.getEmail());
    }

    /**
     * Deletes all AI history records belonging to the current user.
     */
    @Override
    @Transactional
    public void deleteCurrentUserHistory() {
        User user = userService.getAuthenticatedUser();

        aiHistoryRepository.deleteByUser(user);
        log.info("All AI history deleted for user {}", user.getEmail());
    }

    private AiHistoryResponse toResponse(AiHistory history) {
        return new AiHistoryResponse(
                history.getId(),
                history.getPnrNumber(),
                history.getAiResponse(),
                history.getCreatedAt()
        );
    }
}
