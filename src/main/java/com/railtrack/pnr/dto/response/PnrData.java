package com.railtrack.pnr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PnrData {
    private String pnrNumber;
    private String trainNumber;
    private String trainName;
    private String sourceStation;
    private String destinationStation;
    private String boardingPoint;
    private String journeyClass;
    private String dateOfJourney;
    private String chartStatus;
    private Integer bookingFare;
    private Integer ticketFare;
    private Integer distance;
    private List<Passenger> passengerList;
}