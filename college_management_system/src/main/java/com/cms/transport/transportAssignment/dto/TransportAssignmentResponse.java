package com.cms.transport.transportAssignment.dto;

import com.cms.transport.transportAssignment.enums.Shift;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportAssignmentResponse {

    private Long assignmentId;

    private Long busId;
    private String busNumber;

    private Long driverId;
    private String driverName;

    private Long routeId;
    private String routeName;

    private Long gpsId;
    private String gpsSerial;

    private LocalDate startDate;
    private LocalDate endDate;
    private Shift shift;

    private String notes;
    private String status;
}
