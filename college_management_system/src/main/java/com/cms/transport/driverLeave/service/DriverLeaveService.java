package com.cms.transport.driverLeave.service;




import com.cms.transport.driverLeave.dto.DriverLeaveRequest;
import com.cms.transport.driverLeave.dto.DriverLeaveResponse;
import com.cms.transport.driverLeave.enums.ApprovalStatus;

import java.time.LocalDate;
import java.util.List;

public interface DriverLeaveService {

    DriverLeaveResponse createLeave(DriverLeaveRequest request);

    DriverLeaveResponse updateLeave(Long leaveId, DriverLeaveRequest request);

    DriverLeaveResponse getLeave(Long leaveId);

    List<DriverLeaveResponse> getAllLeaves();

    List<DriverLeaveResponse> getLeavesByDriver(Long driverId);

    List<DriverLeaveResponse> getLeavesByApprovalStatus(ApprovalStatus status);

    String deleteLeave(Long leaveId);

    // Admin actions
    DriverLeaveResponse approveLeave(Long leaveId);

    DriverLeaveResponse rejectLeave(Long leaveId);

    // helper used by TransportAssignment
    boolean isDriverOnApprovedLeave(Long driverId, LocalDate date);
}

