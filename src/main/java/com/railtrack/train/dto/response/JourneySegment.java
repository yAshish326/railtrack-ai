package com.railtrack.train.dto.response;

import lombok.Data;

@Data
public class JourneySegment {

    private Station from;

    private Station to;

    private String departureTime;

    private String arrivalTime;

    private String travelTime;
}