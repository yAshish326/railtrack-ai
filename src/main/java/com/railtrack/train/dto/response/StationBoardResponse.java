package com.railtrack.train.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/** Business-level response for a station's train board. */
@Data
@Builder
public class StationBoardResponse {

    private String stationCode;

    private String stationName;

    private LocalDate date;

    private Integer totalTrains;

    private List<StationBoardTrainResponse> trains;
}
