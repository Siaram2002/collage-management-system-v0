package com.cms.transport.dto;



import com.cms.transport.enums.TransportStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransportAssignmentDTO {
    private Long assignmentId;
    private Long busId;
    private Long driverId;
    private Long routeId;
    private LocalDate assignmentDate;
    private TransportStatus status;
}
