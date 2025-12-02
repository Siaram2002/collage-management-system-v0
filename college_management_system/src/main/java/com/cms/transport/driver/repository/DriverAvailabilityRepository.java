package com.cms.transport.driver.repository;

import com.cms.transport.driver.enums.LeaveType;
import com.cms.transport.driver.model.DriverAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DriverAvailabilityRepository extends JpaRepository<DriverAvailability, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Get all availability (leave) records for a driver.
     */
    List<DriverAvailability> findByDriver_DriverId(Long driverId);


    // -----------------------------------------------------
    // 📅 DATE RANGE CHECKS (MOST IMPORTANT)
    // -----------------------------------------------------

    /**
     * Check all leaves overlapping a given date—used to check if
     * driver is available for assignment on that date.
     *
     * If (start <= givenDate <= end), then driver is unavailable.
     */
    List<DriverAvailability> findByDriver_DriverIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long driverId, LocalDate givenDateStart, LocalDate givenDateEnd);


    /**
     * Find all leaves fully inside a given date range:
     * e.g., show all leaves for a specific week/month.
     */
    List<DriverAvailability> findByStartDateBetween(LocalDate start, LocalDate end);


    // -----------------------------------------------------
    // 🗓 EXACT DATE LOOKUPS
    // -----------------------------------------------------

    /**
     * Find leave entries starting on a specific date.
     */
    List<DriverAvailability> findByStartDate(LocalDate startDate);

    /**
     * Find leave entries ending on a specific date.
     */
    List<DriverAvailability> findByEndDate(LocalDate endDate);


    // -----------------------------------------------------
    // 🏷 FILTER BY LEAVE TYPE
    // -----------------------------------------------------

    /**
     * Filter leaves by type (SICK, VACATION, EMERGENCY, etc.)
     */
    List<DriverAvailability> findByLeaveType(LeaveType leaveType);


    // -----------------------------------------------------
    // 📊 REPORT & UI LISTING
    // -----------------------------------------------------

    /**
     * List all availability sorted by creation date (newest first).
     */
    List<DriverAvailability> findAllByOrderByCreatedAtDesc();

    /**
     * List all leaves for a driver sorted by startDate.
     */
    List<DriverAvailability> findByDriver_DriverIdOrderByStartDateAsc(Long driverId);
}
