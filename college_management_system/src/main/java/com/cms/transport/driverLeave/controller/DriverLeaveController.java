package com.cms.transport.driverLeave.controller;

import com.cms.transport.driverLeave.dto.DriverLeaveRequest;
import com.cms.transport.driverLeave.dto.DriverLeaveResponse;
import com.cms.transport.driverLeave.service.DriverLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling Driver Leave Operations.
 *
 * This module allows drivers to apply for leave,
 * admins to approve/reject leave, and the system
 * to check driver availability before transport assignments.
 */
@RestController
@RequestMapping("/api/driver-leaves")
@RequiredArgsConstructor
public class DriverLeaveController {

    private final DriverLeaveService leaveService;

    /**
     * POST: Create a new leave request.
     *
     * - Typically called by Driver or Transport Manager.
     * - All new requests automatically have APPROVAL_STATUS = PENDING.
     * - No admin logic at this stage.
     */
    @PostMapping
    public ResponseEntity<DriverLeaveResponse> create(@RequestBody DriverLeaveRequest request) {
        return ResponseEntity.ok(leaveService.createLeave(request));
    }

    /**
     * PUT: Update an existing leave request.
     *
     * Rules:
     * - A leave request can be UPDATED only if it is still PENDING.
     * - If APPROVED or REJECTED, update is not allowed.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverLeaveResponse> update(
            @PathVariable Long id,
            @RequestBody DriverLeaveRequest request
    ) {
        return ResponseEntity.ok(leaveService.updateLeave(id, request));
    }

    /**
     * GET: Get a single leave record by leaveId.
     *
     * - Returns full leave details including driver info.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverLeaveResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeave(id));
    }

    /**
     * GET: Fetch ALL leave requests (Admin view).
     *
     * - Shows leaves for all drivers.
     * - Useful in leave admin dashboard.
     */
    @GetMapping
    public ResponseEntity<List<DriverLeaveResponse>> getAll() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    /**
     * GET: Fetch all leaves for a specific driver.
     *
     * - Useful in driver profile view.
     * - Shows both approved and pending history.
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<DriverLeaveResponse>> getByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(leaveService.getLeavesByDriver(driverId));
    }

    /**
     * PUT: Approve a leave request.
     *
     * - Only Admin or Transport Manager should call this.
     * - Ensures driver is not assigned to buses during approved leave period.
     * - Updates status → APPROVED.
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<DriverLeaveResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveLeave(id));
    }

    /**
     * PUT: Reject a leave request.
     *
     * - Only Admin can reject.
     * - Updates status → REJECTED.
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<DriverLeaveResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.rejectLeave(id));
    }

    /**
     * DELETE: Delete a leave record.
     *
     * - Typically allowed only for admin users.
     * - Can be used to remove invalid / duplicate leave entries.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.deleteLeave(id));
    }

    /**
     * GET: Utility API to check if a specific driver is on leave on a given date.
     *
     * - Used by TransportAssignmentService BEFORE assigning buses.
     * - Returns TRUE if:
     *      the driver has APPROVED leave covering the requested date.
     * - Else returns FALSE.
     *
     * Example:
     * GET /api/driver-leaves/check/5/2025-02-10
     */
    @GetMapping("/check/{driverId}/{date}")
    public ResponseEntity<Boolean> isOnLeave(
            @PathVariable Long driverId,
            @PathVariable String date
    ) {
        return ResponseEntity.ok(
                leaveService.isDriverOnApprovedLeave(driverId, java.time.LocalDate.parse(date))
        );
    }
}
