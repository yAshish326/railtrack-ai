package com.railtrack.pnr.controller;

import com.railtrack.pnr.dto.response.PnrHistoryResponse;
import com.railtrack.pnr.dto.response.PnrHistoryMessageResponse;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.service.PnrService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles PNR status and authenticated PNR history APIs.
 */
@RestController
@RequestMapping("/api/v1/pnr")
public class PnrController {

    private final PnrService pnrService;

    public PnrController(PnrService pnrService) {
        this.pnrService = pnrService;
    }

    /**
     * Returns the current status for the supplied PNR number.
     */
    @GetMapping("/{pnrNumber}")
    public ResponseEntity<PnrResponse> getPnrStatus(
            @PathVariable
            @Pattern(regexp = "\\d{10}",
                    message = "PNR number must contain exactly 10 digits")
            String pnrNumber) {

        return ResponseEntity.ok(pnrService.getPnrStatus(pnrNumber));
    }

    /**
     * Returns PNR history for the authenticated user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<PnrHistoryResponse>> getPnrHistory() {

        return ResponseEntity.ok(pnrService.getPnrHistory());
    }

    /**
     * Deletes one PNR history record belonging to the authenticated user.
     */
    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<PnrHistoryMessageResponse> deletePnrHistory(
            @PathVariable Long historyId) {

        pnrService.deletePnrHistory(historyId);

        return ResponseEntity.ok(new PnrHistoryMessageResponse(
                "PNR history deleted successfully."
        ));
    }

    /**
     * Deletes all PNR history records belonging to the authenticated user.
     */
    @DeleteMapping("/history")
    public ResponseEntity<PnrHistoryMessageResponse> deleteAllPnrHistory() {

        pnrService.deleteAllPnrHistory();

        return ResponseEntity.ok(new PnrHistoryMessageResponse(
                "All PNR history deleted successfully."
        ));
    }
}
