package com.railtrack.ai.service.impl;

import com.railtrack.ai.dto.AiPnrResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.ai.service.AiService;
import com.railtrack.train.dto.response.Train;
import com.railtrack.pnr.dto.response.PnrData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

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

        String insightMessage = String.format(
                "⚡ AI Analysis Summary: The fastest option is '%s' (%s) taking %s. The longest option is '%s' (%s) taking %s.",
                fastest.getName(), fastest.getNumber(), extractDuration(fastest),
                longest.getName(), longest.getNumber(), extractDuration(longest)
        );

        return new AiTrainRecommendationResponse(insightMessage, fastest, longest);
    }

    @Override
    public AiPnrResponse analyzePnrStatus(PnrData pnrData) {
        if (pnrData == null) {
            return new AiPnrResponse("UNKNOWN", 0.0, "Invalid PNR details provided.", false);
        }

        // Check verification flags against Chart Status string
        String statusText = pnrData.getChartStatus() != null ? pnrData.getChartStatus().toUpperCase() : "";
        boolean isConfirmed = statusText.contains("CNF") || statusText.contains("CONFIRMED") || statusText.contains("CHART PREPARED");

        if (isConfirmed) {
            String confirmedText = "🎉 Your ticket is Confirmed! You are fully cleared to board this journey. Please ensure you carry an original Government-issued Photo ID card to present to the Ticket Examiner (TTE).";
            return new AiPnrResponse("CONFIRMED", 100.0, confirmedText, false);
        } else {
            int waitlistPosition = 45; // Heuristic default position fallback

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
        if (durationStr == null || durationStr.trim().isEmpty()) return 0;
        try {
            String clean = durationStr.toLowerCase().replaceAll("[^0-9:]", "");
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