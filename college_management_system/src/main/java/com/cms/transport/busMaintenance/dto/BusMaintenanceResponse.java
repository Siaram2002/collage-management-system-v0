package com.cms.transport.busMaintenance.dto;

import com.cms.transport.bus.enums.MaintenanceType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusMaintenanceResponse {

    private Long maintenanceId;
    private Long busId;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private MaintenanceType maintenanceType;
    private String remarks;
    private LocalDateTime createdAt;
}
