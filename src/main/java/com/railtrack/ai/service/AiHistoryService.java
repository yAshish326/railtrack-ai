package com.railtrack.ai.service;

import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.auth.entity.User;

import java.util.List;

public interface AiHistoryService {

    /**
     * Saves AI history with PNR number.
     */
    void saveHistory(User user,
                     String pnrNumber,
                     String prompt,
                     String aiResponse);

    /**
     * Saves AI Assistant chat history (without PNR).
     */
    void saveHistory(User user,
                     String prompt,
                     String aiResponse);

    /**
     * Returns AI history for the currently authenticated user.
     */
    List<AiHistoryResponse> getCurrentUserHistory();

    /**
     * Deletes one AI history record.
     */
    void deleteCurrentUserHistory(Long historyId);

    /**
     * Deletes all AI history.
     */
    void deleteCurrentUserHistory();
}