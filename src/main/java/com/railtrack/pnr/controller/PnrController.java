package com.railtrack.pnr.controller;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.pnr.service.PnrService;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pnr")
public class PnrController {

    private final PnrService pnrService;

    public PnrController(PnrService pnrService) {
        this.pnrService = pnrService;
    }

    @GetMapping("/{pnrNumber}")
    public PnrResponse getPnrStatus(
            @PathVariable
            @Pattern(regexp = "\\d{10}",
                    message = "PNR number must contain exactly 10 digits")
            String pnrNumber) {

        return pnrService.getPnrStatus(pnrNumber);
    }
}