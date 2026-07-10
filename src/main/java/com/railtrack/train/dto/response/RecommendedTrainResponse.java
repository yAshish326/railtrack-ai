package com.railtrack.train.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Cacheable(value = "trainSearch", key = "#from + '-' + #to")
public class RecommendedTrainResponse {

    private Train bestTrain;

    private TrainSearchResponse trainSearchResponse;

}