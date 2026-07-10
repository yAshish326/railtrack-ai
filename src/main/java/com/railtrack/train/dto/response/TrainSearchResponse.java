package com.railtrack.train.dto.response;

import lombok.Data;

@Data
public class TrainSearchResponse {

    private Boolean success;

    private TrainData data;

    private Meta meta;

}