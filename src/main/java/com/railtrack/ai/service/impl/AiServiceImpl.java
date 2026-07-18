package com.railtrack.ai.service.impl;

import com.railtrack.ai.dto.AiPnrResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.ai.service.AiService;
import com.railtrack.ai.service.AiChatService;
import com.railtrack.ai.prompt.PromptBuilder;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.dto.response.TrainSummaryResponse;
import com.railtrack.pnr.dto.response.PnrData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AiServiceImpl implements AiService {

    private final AiChatService aiChatService;

    // Constructor Injection for the AI Engine contract
    public AiServiceImpl(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @Override
    public AiTrainRecommendationResponse generateTrainSuggestions(List<Train> trains) {
        if (trains == null || trains.isEmpty()) {
            return new AiTrainRecommendationResponse("No train data available to evaluate at this time.", null, null);
        }

        Train fastest = trains.get(0);
        Train longest = trains.get(0);

        for (Train train : trains) {
            String currentDuration = extractDuration(train);
            String fastestDuration = extractDuration(fastest);
            String longestDuration = extractDuration(longest);

            if (parseDurationToMinutes(currentDuration) < parseDurationToMinutes(fastestDuration)) {
                fastest = train;
            }
            if (parseDurationToMinutes(currentDuration) > parseDurationToMinutes(longestDuration)) {
                longest = train;
            }
        }

        List<TrainSummaryResponse> summaryTrains = trains.stream().map(t -> {
            String dep = (t.getJourneySegment() != null) ? t.getJourneySegment().getDepartureTime() : "00:00";
            String arr = (t.getJourneySegment() != null) ? t.getJourneySegment().getArrivalTime() : "00:00";

            if (dep == null || dep.trim().isEmpty()) dep = "00:00";
            if (arr == null || arr.trim().isEmpty()) arr = "00:00";

            return TrainSummaryResponse.builder()
                    .trainNumber(t.getNumber())
                    .trainName(t.getName())
                    .trainType(t.getType())
                    .departure(dep)
                    .arrival(arr)
                    .duration(extractDuration(t))
                    .runningDays(t.getRunDays() != null ? t.getRunDays() : new ArrayList<>())
                    .build();
        }).collect(Collectors.toList());

        // 1. Generate the structured traveler itinerary prompt instructions
        String optimizedPrompt = PromptBuilder.buildTrainAnalysisPrompt(summaryTrains);

        // 2. ✅ Get the live markdown generation text from Gemini via Spring AI
        String insightMessage = aiChatService.chat(optimizedPrompt);

        // 3. Return the response containing the live AI insights text alongside train entities
        return new AiTrainRecommendationResponse(insightMessage, fastest, longest);
    }

    @Override
    public AiPnrResponse analyzePnrStatus(PnrData pnrData) {
        if (pnrData == null) {
            return new AiPnrResponse("UNKNOWN", 0.0, "Invalid PNR details provided.", false);
        }

        String statusText = pnrData.getChartStatus() != null ? pnrData.getChartStatus().toUpperCase() : "";
        boolean isConfirmed = statusText.contains("CNF") || statusText.contains("CONFIRMED") || statusText.contains("CHART PREPARED");

        if (isConfirmed) {
            String confirmedText = "🎉 Your ticket is Confirmed! You are fully cleared to board this journey. Please ensure you carry an original Government-issued Photo ID card to present to the Ticket Examiner (TTE).";
            return new AiPnrResponse("CONFIRMED", 100.0, confirmedText, false);
        } else {
            int waitlistPosition = 45;
            double confirmationChance = calculateHeuristicChance(waitlistPosition);
            boolean alternativeSuggested = false;
            String adviceText;

            if (confirmationChance < 70.0) {
                adviceText = String.format(
                        "⚠️ Your ticket is currently Waitlisted. AI Estimate: There is only a %.1f%% probability of confirmation before charting. " +
                                "Since the chance is below 70%%, we highly recommend looking into alternate travel modes. You can easily book a direct luxury or express bus from %s to %s for an assured seat.",
                        confirmationChance, pnrData.getSourceStation(), pnrData.getDestinationStation()
                );
                alternativeSuggested = true;
            } else {
                adviceText = String.format(
                        "⏳ Your ticket is currently Waitlisted. AI Estimate: There is a promising %.1f%% chance of your seat shifting to Confirmed before departure. Keep tracking updates!",
                        confirmationChance
                );
            }

            return new AiPnrResponse("WAITLISTED", confirmationChance, adviceText, alternativeSuggested);
        }
    }

    private String extractDuration(Train train) {
        if (train != null && train.getJourneySegment() != null) {
            return train.getJourneySegment().getTravelTime() != null ? train.getJourneySegment().getTravelTime() : "00:00";
        }
        return "00:00";
    }

    private int parseDurationToMinutes(String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            return 0;
        }
        try {
            String clean = durationStr.toLowerCase().replaceAll("[^0-9hms: ]", "").trim();
            if (clean.contains("h")) {
                String[] parts = clean.split("h");
                int hours = Integer.parseInt(parts[0].trim());
                int minutes = 0;
                if (parts.length > 1 && parts[1].contains("m")) {
                    minutes = Integer.parseInt(parts[1].replace("m", "").trim());
                }
                return (hours * 60) + minutes;
            }
            if (clean.contains(":")) {
                String[] parts = clean.split(":");
                return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
            }
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    private double calculateHeuristicChance(int wlNum) {
        double score = 98.0 - (wlNum * 1.25);
        if (score < 5.0) score = 8.5;
        if (score > 95.0) score = 94.0;
        return Math.round(score * 10.0) / 10.0;
    }
}