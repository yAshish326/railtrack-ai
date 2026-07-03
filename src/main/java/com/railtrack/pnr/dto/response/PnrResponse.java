package com.railtrack.pnr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PnrResponse {

    private Boolean success;

    private PnrData data;

    private Long generatedTimeStamp;
}