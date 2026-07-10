package com.railtrack.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationSummary {

    private String title;

    private String reason;

    private List<String> advantages;

    private List<String> travelTips;

}