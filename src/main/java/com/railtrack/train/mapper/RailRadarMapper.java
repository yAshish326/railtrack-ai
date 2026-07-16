package com.railtrack.train.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.train.dto.response.TrainDetailsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



@Component
public class RailRadarMapper {

    public TrainDetailsResponse mapTrainDetails(RailRadarResponse response) {

        if (response == null || !response.success() || response.data() == null) {
            return null;
        }

        JsonNode train = response.data().get("train");

        if (train == null || train.isNull()) {
            return null;
        }

        TrainDetailsResponse dto = TrainDetailsResponse.builder()

                .trainNumber(getText(train, "trainNumber"))
                .trainName(getText(train, "trainName"))
                .trainType(getText(train, "type"))

                .sourceStationCode(getText(train, "sourceStationCode"))
                .sourceStationName(getText(train, "sourceStationName"))

                .destinationStationCode(getText(train, "destinationStationCode"))
                .destinationStationName(getText(train, "destinationStationName"))

                .distanceKm(getDouble(train, "distanceKm"))
                .travelTimeMinutes(getInt(train, "travelTimeMinutes"))
                .totalHalts(getInt(train, "totalHalts"))

                .runningDays(extractRunningDays(train))

                .build();

        return dto;
    }

    private List<String> extractRunningDays(JsonNode train) {

        List<String> days = new ArrayList<>();

        JsonNode runningDays = train.get("runningDays");

        if (runningDays == null) {
            return days;
        }

        JsonNode array = runningDays.get("days");

        if (array == null || !array.isArray()) {
            return days;
        }

        Iterator<JsonNode> iterator = array.elements();

        while (iterator.hasNext()) {
            days.add(iterator.next().asText());
        }

        return days;
    }

    private String getText(JsonNode node, String field) {

        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asText();
    }

    private Integer getInt(JsonNode node, String field) {

        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asInt();
    }

    private Double getDouble(JsonNode node, String field) {

        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asDouble();
    }

}