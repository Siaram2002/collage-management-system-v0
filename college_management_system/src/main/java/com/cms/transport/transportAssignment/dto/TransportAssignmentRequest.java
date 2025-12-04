package com.cms.transport.transportAssignment.dto;

import com.cms.transport.transportAssignment.enums.Shift;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportAssignmentRequest {

    private Long busId;
    private Long driverId;
    private Long routeId;
    private Long gpsId; // optional

    private LocalDate startDate;
    private LocalDate endDate;

    private Shift shift; // MORNING / AFTERNOON / FULL_DAY

    private String notes;

}
