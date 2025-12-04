package com.cms.transport.driverLeave.dto;


import com.cms.common.enums.Status;
import com.cms.transport.driverLeave.enums.ApprovalStatus;
import com.cms.transport.driverLeave.enums.LeaveType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DriverLeaveResponse {
    private Long leaveId;
    private Long driverId;
    private String driverName;      // optional convenience
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private ApprovalStatus approvalStatus;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

