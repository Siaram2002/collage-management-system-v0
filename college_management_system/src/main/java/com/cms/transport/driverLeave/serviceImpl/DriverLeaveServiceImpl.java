package com.cms.transport.driverLeave.serviceImpl;


import com.cms.common.enums.Status;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.driverLeave.dto.DriverLeaveRequest;
import com.cms.transport.driverLeave.dto.DriverLeaveResponse;
import com.cms.transport.driverLeave.enums.ApprovalStatus;
import com.cms.transport.driverLeave.model.DriverLeave;
import com.cms.transport.driverLeave.repository.DriverLeaveRepository;
import com.cms.transport.driverLeave.service.DriverLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverLeaveServiceImpl implements DriverLeaveService {

    private final DriverLeaveRepository leaveRepo;
    private final DriverRepository driverRepo;

    @Override
    public DriverLeaveResponse createLeave(DriverLeaveRequest request) {
        Driver driver = driverRepo.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found: " + request.getDriverId()));

        if (request.getStartDate() == null || request.getEndDate() == null ||
                request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Invalid date range for leave");
        }

        // Optionally: check overlapping approved leaves and block creation if overlapping approved exist
        boolean overlapApproved = leaveRepo.existsByDriverAndApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                driver, ApprovalStatus.APPROVED, request.getEndDate(), request.getStartDate());
        if (overlapApproved) {
            throw new RuntimeException("Driver already has an approved leave overlapping this period");
        }

        DriverLeave leave = DriverLeave.builder()
                .driver(driver)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaveType(request.getLeaveType())
                .notes(request.getNotes())
                .approvalStatus(ApprovalStatus.PENDING)
                .status(Status.ACTIVE)
                .build();

        DriverLeave saved = leaveRepo.save(leave);
        return toDto(saved);
    }

    @Override
    public DriverLeaveResponse updateLeave(Long leaveId, DriverLeaveRequest request) {
        DriverLeave existing = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found: " + leaveId));

        if (existing.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new RuntimeException("Cannot modify an already approved leave");
        }

        Driver driver = driverRepo.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found: " + request.getDriverId()));

        if (request.getStartDate() == null || request.getEndDate() == null ||
                request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Invalid date range for leave");
        }

        existing.setDriver(driver);
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setLeaveType(request.getLeaveType());
        existing.setNotes(request.getNotes());

        DriverLeave updated = leaveRepo.save(existing);
        return toDto(updated);
    }

    @Override
    public DriverLeaveResponse getLeave(Long leaveId) {
        return leaveRepo.findById(leaveId).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Leave not found: " + leaveId));
    }

    @Override
    public List<DriverLeaveResponse> getAllLeaves() {
        return leaveRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DriverLeaveResponse> getLeavesByDriver(Long driverId) {
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
        return leaveRepo.findByDriver(driver).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DriverLeaveResponse> getLeavesByApprovalStatus(ApprovalStatus status) {
        return leaveRepo.findByApprovalStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public String deleteLeave(Long leaveId) {
        if (!leaveRepo.existsById(leaveId)) throw new RuntimeException("Leave not found");
        leaveRepo.deleteById(leaveId);
        return "Driver leave deleted successfully.";
    }

    @Override
    public DriverLeaveResponse approveLeave(Long leaveId) {
        DriverLeave leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found: " + leaveId));

        if (leave.getApprovalStatus() == ApprovalStatus.APPROVED) return toDto(leave);

        // Optional: check assignments overlapping this leave, and notify or prevent approval based on business rules

        leave.setApprovalStatus(ApprovalStatus.APPROVED);
        DriverLeave saved = leaveRepo.save(leave);
        return toDto(saved);
    }

    @Override
    public DriverLeaveResponse rejectLeave(Long leaveId) {
        DriverLeave leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found: " + leaveId));

        if (leave.getApprovalStatus() == ApprovalStatus.REJECTED) return toDto(leave);

        leave.setApprovalStatus(ApprovalStatus.REJECTED);
        DriverLeave saved = leaveRepo.save(leave);
        return toDto(saved);
    }

    @Override
    public boolean isDriverOnApprovedLeave(Long driverId, LocalDate date) {
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));

        return leaveRepo.existsByDriverAndApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                driver, ApprovalStatus.APPROVED, date, date);
    }

    private DriverLeaveResponse toDto(DriverLeave l) {
        String driverName = null;
        try {
            driverName = l.getDriver().getFullName(); // adjust if different field
        } catch (Exception ignored) {}

        return DriverLeaveResponse.builder()
                .leaveId(l.getLeaveId())
                .driverId(l.getDriver().getDriverId())
                .driverName(driverName)
                .startDate(l.getStartDate())
                .endDate(l.getEndDate())
                .leaveType(l.getLeaveType())
                .approvalStatus(l.getApprovalStatus())
                .status(l.getStatus())
                .notes(l.getNotes())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }
}

