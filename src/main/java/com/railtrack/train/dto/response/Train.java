package com.railtrack.train.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class Train {
    private String number;
    private String name;
    private String type;
    private String nameHindi;
    private String typeDescription;
    private String typeDescriptionHindi;
    private Station source;
    private Station destination;
    private List<String> runDays;
    private List<String> availableClasses;
    private JourneySegment journeySegment;
}