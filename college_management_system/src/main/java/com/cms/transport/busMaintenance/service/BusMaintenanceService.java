package com.cms.transport.busMaintenance.service;

import com.cms.transport.busMaintenance.dto.BusMaintenanceRequest;
import com.cms.transport.busMaintenance.dto.BusMaintenanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface BusMaintenanceService {

    BusMaintenanceResponse createMaintenance(BusMaintenanceRequest request);

    BusMaintenanceResponse updateMaintenance(Long maintenanceId, BusMaintenanceRequest request);

    BusMaintenanceResponse getMaintenance(Long maintenanceId);

    List<BusMaintenanceResponse> getAllMaintenance();

    List<BusMaintenanceResponse> getMaintenanceByBus(Long busId);

    String deleteMaintenance(Long maintenanceId);

    // Method to check bus availability
    boolean isBusAvailableForMaintenance(Long busId, LocalDate startDate, LocalDate endDate);
}
