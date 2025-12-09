package com.cms.transport.service;

import com.cms.transport.bus.enums.BusStatus;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.bus.repositories.BusRepository;
import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.dto.BusGpsDto;
import com.cms.transport.dto.TransportAssignmentDTO;
import com.cms.transport.models.TransportAssignment;
import com.cms.transport.repositories.TransportAssignmentRepository;
import com.cms.transport.route.models.Route;
import com.cms.transport.route.repository.RouteRepository;
import com.cms.common.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportAssignmentServiceImpl implements TransportAssignmentService {

    private final TransportAssignmentRepository assignmentRepo;
    private final TransportAssignmentAuditService auditService;
    private final BusRepository busRepo;
    private final DriverRepository driverRepo;
    private final RouteRepository routeRepo;

    // ================= CREATE =================
    @Override
    @Transactional
    public TransportAssignment createAssignment(TransportAssignmentDTO dto) {

        log.info("Creating assignment: Bus={} Driver={} Route={} Date={}",
                dto.getBusId(), dto.getDriverId(), dto.getRouteId(), dto.getAssignmentDate());

        Bus bus = busRepo.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Driver driver = driverRepo.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        Route route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        // ----------------- STATUS CHECK -----------------
        if (bus.getStatus() != BusStatus.ACTIVE) {
            throw new IllegalArgumentException("Bus is not ACTIVE and cannot be assigned");
        }

        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new IllegalArgumentException("Driver is not ACTIVE and cannot be assigned");
        }

        // ----------------- CONFLICT CHECK -----------------
        if (assignmentRepo.findByDriverAndStatus(driver, dto.getStatus()).isPresent()) {
            throw new IllegalArgumentException("Driver already has an active assignment");
        }

        if (assignmentRepo.findByBusAndStatus(bus, dto.getStatus()).isPresent()) {
            throw new IllegalArgumentException("Bus already has an active assignment");
        }

        // ----------------- CREATE ASSIGNMENT -----------------
        TransportAssignment assignment = TransportAssignment.builder()
                .bus(bus)
                .driver(driver)
                .route(route)
                .assignmentDate(dto.getAssignmentDate())
                .status(dto.getStatus())
                .build();

        TransportAssignment saved = assignmentRepo.save(assignment);

        // ----------------- UPDATE DRIVER & BUS -----------------
        driver.setAssignment(saved);
        bus.setAssignment(saved);

        driverRepo.save(driver);
        busRepo.save(bus);

        // ----------------- AUDIT -----------------
        auditService.saveAudit(null, saved);

        return saved;
    }


    // ================= UPDATE =================
    @Override
    @Transactional
    public TransportAssignment updateAssignment(Long id, TransportAssignmentDTO dto) {

        log.info("Updating assignment {}", id);

        // Fetch existing assignment
        TransportAssignment existing = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Fetch bus, driver, and route
        Bus bus = busRepo.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Driver driver = driverRepo.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        Route route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        // ----------------- STATUS CHECK -----------------
        if (bus.getStatus() != BusStatus.ACTIVE) {
            throw new IllegalArgumentException("Bus is not ACTIVE and cannot be assigned");
        }

        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new IllegalArgumentException("Driver is not ACTIVE and cannot be assigned");
        }

        // ----------------- CONFLICT CHECK -----------------
        assignmentRepo.findByDriverAndStatus(driver, dto.getStatus())
                .filter(a -> !a.getAssignmentId().equals(existing.getAssignmentId()))
                .ifPresent(a -> { throw new IllegalArgumentException("Driver already has an active assignment"); });

        assignmentRepo.findByBusAndStatus(bus, dto.getStatus())
                .filter(a -> !a.getAssignmentId().equals(existing.getAssignmentId()))
                .ifPresent(a -> { throw new IllegalArgumentException("Bus already has an active assignment"); });

        // ----------------- SNAPSHOT FOR AUDIT -----------------
        TransportAssignment oldSnapshot = TransportAssignment.builder()
                .assignmentId(existing.getAssignmentId())
                .bus(existing.getBus())
                .driver(existing.getDriver())
                .route(existing.getRoute())
                .assignmentDate(existing.getAssignmentDate())
                .status(existing.getStatus())
                .build();

        // ----------------- APPLY UPDATES -----------------
        existing.setBus(bus);
        existing.setDriver(driver);
        existing.setRoute(route);
        existing.setAssignmentDate(dto.getAssignmentDate());
        existing.setStatus(dto.getStatus());

        TransportAssignment updated = assignmentRepo.save(existing);

        // ----------------- UPDATE DRIVER & BUS -----------------
        driver.setAssignment(updated);
        bus.setAssignment(updated);

        driverRepo.save(driver);
        busRepo.save(bus);

        // ----------------- AUDIT -----------------
        auditService.saveAudit(oldSnapshot, updated);

        return updated;
    }

    // ================= GETTERS =================
    @Override
    public TransportAssignment getAssignment(Long id) {
        return assignmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }

    @Override
    public List<TransportAssignment> getAllAssignments() {
        return assignmentRepo.findAll();
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteAssignment(Long id) {

        TransportAssignment existing = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        TransportAssignment snapshot = TransportAssignment.builder()
                .assignmentId(existing.getAssignmentId())
                .bus(existing.getBus())
                .driver(existing.getDriver())
                .route(existing.getRoute())
                .assignmentDate(existing.getAssignmentDate())
                .status(existing.getStatus())
                .build();

        // Clear current assignment in driver and bus
        Driver driver = existing.getDriver();
        Bus bus = existing.getBus();

        if (driver != null) {
            driver.setAssignment(null);
            driverRepo.save(driver);
        }
        if (bus != null) {
            bus.setAssignment(null);
            busRepo.save(bus);
        }

        assignmentRepo.delete(existing);

        auditService.saveAudit(snapshot, null);
    }
    
    
    @Override
    public List<BusGpsDto> getBusesByRoute(Long routeId) {

        // Validate route exists
        routeRepo.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        // Get assignments for this route
        List<TransportAssignment> assignments = assignmentRepo.findByRouteRouteId(routeId);

        // Convert to BusGpsDto
        return assignments.stream()
                .map((TransportAssignment a) -> { // Explicit type
                    Bus bus = a.getBus();
                    Driver driver = a.getDriver();
                

                    return BusGpsDto.builder()
                            .busId(bus.getBusId())
                            .busNumber(bus.getBusNumber())
                          
                            .driverId(driver.getDriverId())
                            .driverName(driver.getFullName())
                            .driverPhone(driver.getContact() != null ? driver.getContact().getPhone() : null)
                            .driverEmail(driver.getContact() != null ? driver.getContact().getEmail() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }





}
