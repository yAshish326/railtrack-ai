package com.railtrack.train.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TrainData {

    private Station fromStation;

    private Station toStation;

    private Integer totalTrains;

    private String trainTypeFilter;

    private List<Train> trains;

}