package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Business-level response for the between-stations journey search. */
@Data
@Builder
public class JourneyResponse {

    private String source;

    private String destination;

    private Integer totalTrains;

    private List<TrainSummaryResponse> trains;
}
