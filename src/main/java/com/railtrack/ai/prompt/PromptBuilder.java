package com.railtrack.ai.prompt;

import com.railtrack.pnr.dto.response.Passenger;
import com.railtrack.pnr.dto.response.PnrResponse;

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
}