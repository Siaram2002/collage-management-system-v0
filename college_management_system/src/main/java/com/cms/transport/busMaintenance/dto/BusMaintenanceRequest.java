package com.cms.transport.busMaintenance.dto;

import com.cms.transport.bus.enums.MaintenanceType;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusMaintenanceRequest {

    private Long busId;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private MaintenanceType maintenanceType;
    private String remarks;
}
