package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StationBoardResponse {

    private String stationCode;

    private List<TrainSummaryResponse> trains;
}