package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveTrainResponse {

    private String trainNumber;

    private String currentStation;

    private String nextStation;

    private Integer delayMinutes;

    private Double latitude;

    private Double longitude;

    private String status;
}