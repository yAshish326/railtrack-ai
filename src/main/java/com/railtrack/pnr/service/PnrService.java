package com.railtrack.pnr.service;
import com.railtrack.pnr.dto.response.PnrResponse;

public interface PnrService {

    PnrResponse getPnrStatus(String pnrNumber);

}