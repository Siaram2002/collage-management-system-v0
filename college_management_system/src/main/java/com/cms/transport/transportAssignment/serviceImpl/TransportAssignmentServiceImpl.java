package com.cms.transport.transportAssignment.serviceImpl;

import com.cms.common.enums.Status;
import com.cms.transport.GPSDevice.models.GPSDevice;
import com.cms.transport.GPSDevice.repository.GPSDeviceRepository;
import com.cms.transport.bus.model.Bus;
import com.cms.transport.bus.repository.BusRepository;
import com.cms.transport.busMaintenance.repository.BusMaintenanceRepository;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.driverLeave.repository.DriverLeaveRepository;
import com.cms.transport.route.model.Route;
import com.cms.transport.route.repository.RouteRepository;
import com.cms.transport.transportAssignment.dto.TransportAssignmentRequest;
import com.cms.transport.transportAssignment.dto.TransportAssignmentResponse;
import com.cms.transport.transportAssignment.model.TransportAssignment;
import com.cms.transport.transportAssignment.repository.TransportAssignmentRepository;
import com.cms.transport.transportAssignment.service.TransportAssignmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for TransportAssignment.
 * Handles CRUD operations with validation:
 * - Driver leave check
 * - Bus maintenance check
 * - Driver & Bus conflict check
 * - Soft delete
 */
@Service
@RequiredArgsConstructor
public class TransportAssignmentServiceImpl implements TransportAssignmentService {

    private final TransportAssignmentRepository assignmentRepo;
    private final BusRepository busRepo;
    private final DriverRepository driverRepo;
    private final RouteRepository routeRepo;
    private final GPSDeviceRepository gpsRepo;
    private final DriverLeaveRepository driverLeaveRepo;
    private final BusMaintenanceRepository busMaintenanceRepo;

    // ----------------------------------------------------
    // CREATE ASSIGNMENT
    // ----------------------------------------------------
    @Override
    public TransportAssignmentResponse createAssignment(TransportAssignmentRequest req) {
        validate(req, null);
        TransportAssignment saved = assignmentRepo.save(build(req, null));
        return toResponse(saved);
    }

    // ----------------------------------------------------
    // UPDATE ASSIGNMENT
    // ----------------------------------------------------
    @Override
    public TransportAssignmentResponse updateAssignment(Long id, TransportAssignmentRequest req) {
        TransportAssignment existing = assignmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        validate(req, id);

        TransportAssignment saved = assignmentRepo.save(build(req, existing));
        return toResponse(saved);
    }

    // ----------------------------------------------------
    // VALIDATION METHOD
    // ----------------------------------------------------
    private void validate(TransportAssignmentRequest req, Long currentId) {
        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();

        if (start.isAfter(end)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        Bus bus = busRepo.findById(req.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));
        Driver driver = driverRepo.findById(req.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Driver Leave Check
        if (driverLeaveRepo.isDriverOnLeave(driver.getDriverId(), start, end)) {
            throw new RuntimeException("Driver is on approved leave for selected dates");
        }

        // Bus Maintenance Check
//        if (busMaintenanceRepo.isBusUnderMaintenance(bus.getBusId(), start, end)) {
//            throw new RuntimeException("Bus is under maintenance during selected dates");
//        }
        // Use the existsByBusAndDateRange method instead
        if (busMaintenanceRepo.existsByBusAndDateRange(bus.getBusId(), start, end)) {
            throw new RuntimeException("Bus is under maintenance during selected dates");
        }

        // Driver Conflict Check
        if (assignmentRepo.driverConflict(driver.getDriverId(), start, end, req.getShift(), currentId)) {
            throw new RuntimeException("Driver already assigned in same shift");
        }

        // Bus Conflict Check
        if (assignmentRepo.busConflict(bus.getBusId(), start, end, req.getShift(), currentId)) {
            throw new RuntimeException("Bus already assigned in same shift");
        }
    }

    // ----------------------------------------------------
    // BUILD ENTITY
    // ----------------------------------------------------
    private TransportAssignment build(TransportAssignmentRequest req, TransportAssignment existing) {
        Bus bus = busRepo.findById(req.getBusId()).orElseThrow();
        Driver driver = driverRepo.findById(req.getDriverId()).orElseThrow();
        Route route = routeRepo.findById(req.getRouteId()).orElseThrow();
        GPSDevice gps = (req.getGpsId() != null) ? gpsRepo.findById(req.getGpsId()).orElse(null) : null;

        if (existing == null) {
            return TransportAssignment.builder()
                    .bus(bus)
                    .driver(driver)
                    .route(route)
                    .gpsDevice(gps)
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .shift(req.getShift())
                    .notes(req.getNotes())
                    .status(Status.ACTIVE)
                    .assignmentDate(req.getStartDate())
                    .build();
        }

        existing.setBus(bus);
        existing.setDriver(driver);
        existing.setRoute(route);
        existing.setGpsDevice(gps);
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        existing.setShift(req.getShift());
        existing.setNotes(req.getNotes());

        return existing;
    }

    // ----------------------------------------------------
    // GET ALL ASSIGNMENTS
    // ----------------------------------------------------
    @Override
    public List<TransportAssignmentResponse> getAllAssignments() {
        return assignmentRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // GET ASSIGNMENT BY ID
    // ----------------------------------------------------
    @Override
    public TransportAssignmentResponse getAssignmentById(Long id) {
        TransportAssignment a = assignmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        return toResponse(a);
    }

    // ----------------------------------------------------
    // DELETE ASSIGNMENT (Soft Delete)
    // ----------------------------------------------------
    @Override
    public void deleteAssignment(Long id) {
        TransportAssignment a = assignmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        a.setStatus(Status.INACTIVE);
        assignmentRepo.save(a);
    }

    // ----------------------------------------------------
    // RESPONSE DTO
    // ----------------------------------------------------
    private TransportAssignmentResponse toResponse(TransportAssignment a) {
        return TransportAssignmentResponse.builder()
                .assignmentId(a.getAssignmentId())
                .busId(a.getBus().getBusId())
                .busNumber(a.getBus().getBusNumber())
                .driverId(a.getDriver().getDriverId())
                .driverName(a.getDriver().getFullName())
                .routeId(a.getRoute().getRouteId())
                .routeName(a.getRoute().getRouteName())
                .gpsId(a.getGpsDevice() != null ? a.getGpsDevice().getGpsId() : null)
                .gpsSerial(a.getGpsDevice() != null ? a.getGpsDevice().getDeviceSerialNumber() : null)
                .startDate(a.getStartDate())
                .endDate(a.getEndDate())
                .shift(a.getShift())
                .notes(a.getNotes())
                .status(a.getStatus().name())
                .build();
    }
}
