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

        // 1. Check if passenger list is present and if all are confirmed
        List<com.railtrack.pnr.dto.response.Passenger> passengers = pnrData.getPassengerList();

        if (passengers != null && !passengers.isEmpty()) {
            boolean allConfirmed = passengers.stream().allMatch(p -> {
                String status = p.getCurrentStatus() != null ? p.getCurrentStatus().toUpperCase() : "";
                String details = p.getCurrentStatusDetails() != null ? p.getCurrentStatusDetails().toUpperCase() : "";
                return status.contains("CNF") || status.contains("CONFIRMED") || details.contains("CNF");
            });

            if (allConfirmed) {
                String confirmedText = String.format(
                        "🎉 All passengers on train %s (%s) are fully CONFIRMED. You are ready for your journey from %s to %s!",
                        pnrData.getTrainName(), pnrData.getTrainNumber(),
                        pnrData.getSourceStation(), pnrData.getDestinationStation()
                );
                return new AiPnrResponse("CONFIRMED", 100.0, confirmedText, false);
            }

            // 2. Extract actual Waitlist / RAC numbers dynamically across passengers
            int maxWlNumber = passengers.stream()
                    .map(p -> {
                        String status = p.getCurrentStatus() != null ? p.getCurrentStatus() : p.getBookingStatus();
                        if (status == null) return 0;
                        String digits = status.replaceAll("[^0-9]", "");
                        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
                    })
                    .max(Integer::compare)
                    .orElse(0);

            if (maxWlNumber > 0) {
                double chance = calculateHeuristicChance(maxWlNumber);
                boolean altSuggested = chance < 70.0;

                String adviceText = altSuggested
                        ? String.format("⚠️ Your highest Waitlist position is WL %d. Estimated confirmation chance is %.1f%%. We recommend exploring alternative options from %s to %s.",
                        maxWlNumber, chance, pnrData.getSourceStation(), pnrData.getDestinationStation())
                        : String.format("⏳ Ticket is Waitlisted at WL %d with a promising %.1f%% chance of confirmation before charting.",
                        maxWlNumber, chance);

                return new AiPnrResponse("WAITLISTED", chance, adviceText, altSuggested);
            }
        }

        // 3. Fallback to Chart Status text only if passenger list is unavailable
        String chartText = pnrData.getChartStatus() != null ? pnrData.getChartStatus().toUpperCase() : "";
        if (chartText.contains("CHART PREPARED")) {
            return new AiPnrResponse("CONFIRMED", 100.0, "Chart prepared. Please check final coach allocation.", false);
        }

        return new AiPnrResponse("UNKNOWN", 50.0, "Could not determine precise passenger waitlist status.", false);
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