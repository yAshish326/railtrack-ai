package com.railtrack.pnr.service;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.dto.response.PnrHistoryResponse;

import java.util.List;

public interface PnrService {

    /**
     * Returns the latest PNR status and stores it for the authenticated user.
     */
    PnrResponse getPnrStatus(String pnrNumber);

    /**
     * Returns PNR history for the authenticated user.
     */
    List<PnrHistoryResponse> getPnrHistory();

    /**
     * Deletes one PNR history record belonging to the authenticated user.
     */
    void deletePnrHistory(Long historyId);

    /**
     * Deletes all PNR history records belonging to the authenticated user.
     */
    void deleteAllPnrHistory();

}
