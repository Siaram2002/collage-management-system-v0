package com.cms.transport.dto;

import com.cms.transport.bus.models.Bus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.enums.TransportStatus;
import com.cms.transport.route.models.Route;
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
    
    // Nested objects for frontend
    private Driver driver;
    private Bus bus;
    private Route route;
}
