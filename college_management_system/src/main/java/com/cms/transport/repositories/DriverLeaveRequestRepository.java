package com.cms.transport.repositories;

import com.cms.transport.bus.enums.TripType;
import com.cms.transport.driver.enums.LeaveStatus;
import com.cms.transport.driver.enums.LeaveType;
import com.cms.transport.driver.model.DriverLeaveRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DriverLeaveRequestRepository extends JpaRepository<DriverLeaveRequest, Long> {

    // -----------------------------------------------------
    // 🔍 FINDERS BY DRIVER
    // -----------------------------------------------------

    /**
     * Get all leave requests submitted by a particular driver.
     */
    List<DriverLeaveRequest> findByDriver_DriverId(Long driverId);


    /**
     * Get leave requests for a driver within a date range.
     */
    List<DriverLeaveRequest> findByDriver_DriverIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Long driverId, LocalDate from, LocalDate to);


    // -----------------------------------------------------
    // 📅 DATE RANGE CHECKS (IMPORTANT FOR OVERLAP)
    // -----------------------------------------------------

    /**
     * Check any pending/approved leave request overlapping given date range.
     *
     * Used to prevent duplicate leave requests for same dates.
     */
    List<DriverLeaveRequest> findByDriver_DriverIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long driverId, LocalDate startDate, LocalDate endDate);


    // -----------------------------------------------------
    // 🚦 FILTER BY STATUS
    // -----------------------------------------------------

    /**
     * Get all leave requests by status (PENDING / APPROVED / REJECTED).
     */
    List<DriverLeaveRequest> findByStatus(LeaveStatus status);


    /**
     * All pending requests sorted by request date — for admin dashboard.
     */
    List<DriverLeaveRequest> findByStatusOrderByRequestedAtAsc(LeaveStatus status);


    // -----------------------------------------------------
    // 🏷 FILTER BY LEAVE TYPE
    // -----------------------------------------------------

    /**
     * Get all requests of a given leave type (SICK / PERSONAL).
     */
    List<DriverLeaveRequest> findByLeaveType(LeaveType leaveType);


    // -----------------------------------------------------
    // 🚌 FILTER BY SHIFT (Morning/Evening)
    // -----------------------------------------------------

    /**
     * Get all leave requests for a specific shift.
     */
    List<DriverLeaveRequest> findByShift(TripType shift);


    // -----------------------------------------------------
    // 🔍 FIND BY ADMIN / REVIEWER
    // -----------------------------------------------------

    /**
     * Find all leave requests reviewed by a specific admin.
     */
    List<DriverLeaveRequest> findByReviewedBy_UserId(Long adminId);


    // -----------------------------------------------------
    // 📊 REPORTS & LISTING
    // -----------------------------------------------------

    /**
     * List all leave requests sorted by creation time (newest first).
     */
    List<DriverLeaveRequest> findAllByOrderByRequestedAtDesc();


    /**
     * Get all leaves within a specific date range (for monthly report).
     */
    List<DriverLeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);
}
