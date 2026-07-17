package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Business-level response for a station's live board, grouped by train status. */
@Data
@Builder
public class LiveStationBoardResponse {

    private String stationCode;

    private List<StationBoardTrainResponse> arrivingTrains;

    private List<StationBoardTrainResponse> departingTrains;

    private List<StationBoardTrainResponse> delayedTrains;

    private List<StationBoardTrainResponse> cancelledTrains;
}
