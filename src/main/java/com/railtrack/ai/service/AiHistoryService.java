package com.railtrack.ai.service;

import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.auth.entity.User;

import java.util.List;

public interface AiHistoryService {

    /**
     * Saves a successful AI response for the supplied user.
     */
    void saveHistory(User user,
                     String pnrNumber,
                     String prompt,
                     String aiResponse);

    /**
     * Returns AI history for the currently authenticated user.
     */
    List<AiHistoryResponse> getCurrentUserHistory();

    /**
     * Deletes one AI history record belonging to the current user.
     */
    void deleteCurrentUserHistory(Long historyId);

    /**
     * Deletes all AI history records belonging to the current user.
     */
    void deleteCurrentUserHistory();
}
