package com.railtrack.ai.prompt;

import com.railtrack.pnr.dto.response.Passenger;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.train.dto.response.TrainSummaryResponse;

import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildPnrPrompt(PnrResponse response) {
        StringBuilder passengerInfo = new StringBuilder();

        if (response.getData().getPassengerList() != null) {
            for (Passenger passenger : response.getData().getPassengerList()) {
                passengerInfo.append(String.format("""
                                Passenger %d
                                Booking Status : %s
                                Current Status : %s
                                Coach : %s
                                Berth : %s
                                
                                """,
                        passenger.getPassengerSerialNumber(),
                        passenger.getBookingStatusDetails(),
                        passenger.getCurrentStatusDetails(),
                        passenger.getCurrentCoachId(),
                        passenger.getCurrentBerthNo()));
            }
        }

        return String.format("""
                        You are RailTrack AI, an intelligent Indian Railway travel assistant.
                        
                        Analyze the PNR details below and explain them in a simple, friendly and useful way.
                        
                        ===========================
                        TRAIN DETAILS
                        ===========================
                        Train Name        : %s
                        Train Number      : %s
                        From              : %s
                        To                : %s
                        Boarding Point    : %s
                        Journey Class     : %s
                        Journey Date      : %s
                        Chart Status      : %s
                        Ticket Fare       : ₹%d
                        Distance          : %d KM
                        
                        ===========================
                        PASSENGERS
                        ===========================
                        %s
                        
                        ===========================
                        RESPONSE FORMAT
                        ===========================
                        
                        Ticket Status:
                        <One sentence>
                        
                        Passenger Summary:
                        • Passenger 1 - ...
                        • Passenger 2 - ...
                        
                        Can You Travel?
                        <Yes/No with one reason>
                        
                        Travel Advice:
                        • Bullet 1
                        • Bullet 2
                        
                        IMPORTANT RULES
                        
                        - Maximum 90 words.
                        - Use simple English.
                        - Use short sentences.
                        - Do NOT repeat train information.
                        - Do NOT explain every field.
                        - Never use Markdown symbols such as ** or #.
                        - Do not write long paragraphs.
                        - Sound like a railway assistant helping a passenger.
                        """,
                response.getData().getTrainName(),
                response.getData().getTrainNumber(),
                response.getData().getSourceStation(),
                response.getData().getDestinationStation(),
                response.getData().getBoardingPoint(),
                response.getData().getJourneyClass(),
                response.getData().getDateOfJourney(),
                response.getData().getChartStatus(),
                response.getData().getTicketFare(),
                response.getData().getDistance(),
                passengerInfo.toString()
        );
    }

    /**
     * ✅ Generates a structured prompt for train schedules,
     * calculating precise travel durations to eliminate the 00:00 bug.
     */
    public static String buildTrainAnalysisPrompt(List<TrainSummaryResponse> trains) {
        StringBuilder dataList = new StringBuilder();

        for (TrainSummaryResponse train : trains) {
            String calculatedDuration = "Unknown Duration";

            // Check if your DTO provides a direct duration method or access string
            if (train.getDuration() != null && !train.getDuration().toString().equals("0")) {
                calculatedDuration = train.getDuration().toString();
            } else if (train.getDeparture() != null && train.getArrival() != null) {
                // Safe parsing fallback calculation logic
                try {
                    String[] depParts = train.getDeparture().trim().split(":");
                    String[] arrParts = train.getArrival().trim().split(":");

                    int depMin = Integer.parseInt(depParts[0]) * 60 + Integer.parseInt(depParts[1]);
                    int arrMin = Integer.parseInt(arrParts[0]) * 60 + Integer.parseInt(arrParts[1]);

                    int totalMinutes = arrMin - depMin;
                    if (totalMinutes < 0) {
                        totalMinutes += 24 * 60; // Midnight crossover adjustment handler
                    }

                    calculatedDuration = String.format("%d hours and %d minutes", totalMinutes / 60, totalMinutes % 60);
                } catch (Exception e) {
                    calculatedDuration = "Refer to individual arrival/departure timestamps";
                }
            }

            dataList.append(String.format("- Train Name: %s (%s)\n", train.getTrainName(), train.getTrainNumber()));
            dataList.append(String.format("  Timings: Departs %s, Arrives %s\n", train.getDeparture(), train.getArrival()));
            dataList.append(String.format("  Calculated Duration: %s\n", calculatedDuration));
            dataList.append(String.format("  Runs On: %s\n\n",
                    (train.getRunningDays() != null ? String.join(",", train.getRunningDays()) : "Scheduled Days")));
        }

        return String.format("""
                You are the RailTrack AI Travel Co-pilot. Optimize the user's travel itinerary using this live schedule data:

                %s

                Provide a highly productive, structured evaluation for the passenger exactly following these formats:

                🚀 FASTEST ROUTE: Name the train with the shortest duration. Explain how much time it saves compared to the others.

                🛌 BEST OVERNIGHT OPTION: If any train departs in the evening/afternoon and arrives in the morning, highlight it as the ideal 'sleep-and-travel' option to save a hotel night.

                📊 TRAVEL OPTIMIZATION SCORE: Give a quick, 1-sentence recommendation on whether the user should book right away or look for alternate dates based on train availability/frequencies shown.

                Guardrails:
                - Do not use Markdown styling blocks like ** or #.
                - Do not use '00:00' under any circumstances. Use the 'Calculated Duration' provided above.
                - Keep it to 3 bullet points max. Make it scannable in 5 seconds.
                """,
                dataList.toString()
        );
    }
}
