package com.railtrack.pnr.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    private Integer passengerSerialNumber;
    private String bookingStatus;
    private Integer bookingBerthNo;
    private String bookingStatusDetails;

    private String currentStatus;
    private String currentCoachId;
    private Integer currentBerthNo;
    private String currentStatusDetails;
}