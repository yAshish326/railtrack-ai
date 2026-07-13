package com.railtrack.train.util;

import com.railtrack.train.dto.response.Train;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TrainRecommendationUtil {

    public Train getBestTrain(List<Train> trains) {

        if (trains == null || trains.isEmpty()) {
            return null;
        }

        return trains.stream()
                .sorted(Comparator
                        .comparingInt(this::getPriority)
                        .thenComparingInt(this::travelMinutes))
                .findFirst()
                .orElse(trains.get(0));
    }

    /**
     * Smaller number = Higher Priority
     */
    private int getPriority(Train train) {

        if (train.getType() == null) {
            return 100;
        }

        String type = train.getType().toUpperCase();

        return switch (type) {

            case "VANDE BHARAT" -> 1;
            case "RAJDHANI" -> 2;
            case "SHATABDI" -> 3;
            case "DURONTO" -> 4;
            case "HUMSAFAR" -> 5;
            case "TEJAS" -> 6;
            case "GARIB RATH" -> 7;
            case "SUPERFAST" -> 8;
            case "MAIL", "EXPRESS" -> 9;
            default -> 20;
        };
    }
    /**
     * Converts HH:MM into total minutes
     */
    private int travelMinutes(Train train) {

        if (train.getJourneySegment() == null
                || train.getJourneySegment().getTravelTime() == null) {

            return Integer.MAX_VALUE;
        }

        try {

            String[] time = train.getJourneySegment()
                    .getTravelTime()
                    .split(":");

            return Integer.parseInt(time[0]) * 60
                    + Integer.parseInt(time[1]);

        } catch (Exception e) {

            return Integer.MAX_VALUE;
        }
    }

}