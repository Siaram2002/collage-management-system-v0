package com.cms.transport.busMaintenance.serviceImpl;

import com.cms.transport.busMaintenance.dto.BusMaintenanceRequest;
import com.cms.transport.busMaintenance.dto.BusMaintenanceResponse;
import com.cms.transport.busMaintenance.model.BusMaintenance;
import com.cms.transport.busMaintenance.repository.BusMaintenanceRepository;
import com.cms.transport.bus.model.Bus;
import com.cms.transport.bus.repository.BusRepository;
import com.cms.transport.busMaintenance.service.BusMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusMaintenanceServiceImpl implements BusMaintenanceService {

    private final BusMaintenanceRepository maintenanceRepo;
    private final BusRepository busRepo;

    @Override
    public BusMaintenanceResponse createMaintenance(BusMaintenanceRequest request) {
        Bus bus = busRepo.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        BusMaintenance maintenance = BusMaintenance.builder()
                .bus(bus)
                .startDate(request.getStartDate())
                .expectedEndDate(request.getExpectedEndDate())
                .maintenanceType(request.getMaintenanceType())
                .remarks(request.getRemarks())
                .build();

        BusMaintenance saved = maintenanceRepo.save(maintenance);
        return convertToResponse(saved);
    }

    @Override
    public BusMaintenanceResponse updateMaintenance(Long maintenanceId, BusMaintenanceRequest request) {
        BusMaintenance maintenance = maintenanceRepo.findById(maintenanceId)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));

        Bus bus = busRepo.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        maintenance.setBus(bus);
        maintenance.setStartDate(request.getStartDate());
        maintenance.setExpectedEndDate(request.getExpectedEndDate());
        maintenance.setMaintenanceType(request.getMaintenanceType());
        maintenance.setRemarks(request.getRemarks());

        BusMaintenance updated = maintenanceRepo.save(maintenance);
        return convertToResponse(updated);
    }

    @Override
    public BusMaintenanceResponse getMaintenance(Long maintenanceId) {
        return maintenanceRepo.findById(maintenanceId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
    }

    @Override
    public List<BusMaintenanceResponse> getAllMaintenance() {
        return maintenanceRepo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusMaintenanceResponse> getMaintenanceByBus(Long busId) {
        return maintenanceRepo.findByBusBusId(busId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteMaintenance(Long maintenanceId) {
        if (!maintenanceRepo.existsById(maintenanceId)) {
            throw new RuntimeException("Maintenance ID not found");
        }
        maintenanceRepo.deleteById(maintenanceId);
        return "Maintenance record deleted successfully.";
    }

    private BusMaintenanceResponse convertToResponse(BusMaintenance m) {
        return BusMaintenanceResponse.builder()
                .maintenanceId(m.getMaintenanceId())
                .busId(m.getBus().getBusId())
                .startDate(m.getStartDate())
                .expectedEndDate(m.getExpectedEndDate())
                .maintenanceType(m.getMaintenanceType())
                .remarks(m.getRemarks())
                .createdAt(m.getCreatedAt())
                .build();
    }

    @Override
    public boolean isBusAvailableForMaintenance(Long busId, LocalDate startDate, LocalDate endDate) {
        // true if bus is free
        return !maintenanceRepo.existsByBusAndDateRange(busId, startDate, endDate);
    }



}
