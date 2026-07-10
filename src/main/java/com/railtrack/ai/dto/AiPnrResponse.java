package com.railtrack.ai.dto;

import com.railtrack.pnr.dto.response.PnrResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiPnrResponse {

    private PnrResponse pnrResponse;

    private String aiExplanation;

    private LocalDateTime generatedAt;

    private String model;
}