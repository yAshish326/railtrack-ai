package com.railtrack.ai.prompt;

import com.railtrack.pnr.dto.response.Passenger;
import com.railtrack.pnr.dto.response.PnrResponse;

public class PromptBuilder {

    private PromptBuilder() {
        // Utility class
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
                        passenger.getCurrentBerthNo()
                ));
            }
        }

        return String.format("""
                You are an expert Indian Railway Assistant.

                Explain the following PNR information in simple English.

                Train Name : %s
                Train Number : %s
                Source Station : %s
                Destination Station : %s
                Boarding Point : %s
                Journey Class : %s
                Journey Date : %s
                Chart Status : %s
                Ticket Fare : ₹%d
                Distance : %d KM

                Passenger Details:
                %s

                Instructions:

                1. Explain whether the ticket is Confirmed, RAC or Waiting.
                2. Explain the passenger seat information.
                3. Tell whether the passenger can travel.
                4. Mention any important travel advice.
                5. Keep the response under 150 words.
                6. Respond in simple English.
                7. Do not use Markdown.
                8. Be polite and encouraging.
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