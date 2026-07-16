package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainSummaryResponse {

    private String trainNumber;

    private String trainName;

    private String source;

    private String destination;

    private String departure;

    private String arrival;

    private String duration;
}