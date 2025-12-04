package com.cms.transport.driverLeave.dto;

import com.cms.transport.driverLeave.enums.LeaveType;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DriverLeaveRequest {
    private Long driverId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private String notes;
}

