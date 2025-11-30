package com.cms.transport.models.repositories;

import com.cms.common.enums.Status;
import com.cms.transport.models.BusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusAssignmentRepository extends JpaRepository<BusAssignment, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find assignment by bus, date and active flag.
     * Ensures only one active assignment for a bus on a specific day.
     */
    Optional<BusAssignment> findByBus_BusIdAndAssignmentDateAndActiveFlag(
            Long busId, LocalDate assignmentDate, Integer activeFlag);


    /**
     * Find assignment by driver, date and active flag.
     * Ensures a driver has only one active assignment per day.
     */
    Optional<BusAssignment> findByDriver_DriverIdAndAssignmentDateAndActiveFlag(
            Long driverId, LocalDate assignmentDate, Integer activeFlag);


    // -----------------------------------------------------
    // 🚍 QUERY BY BUS / DRIVER / ROUTE
    // -----------------------------------------------------

    /**
     * List all active assignments for a bus.
     */
    List<BusAssignment> findByBus_BusIdAndStatus(Long busId, Status status);

    /**
     * List all active assignments of a driver.
     */
    List<BusAssignment> findByDriver_DriverIdAndStatus(Long driverId, Status status);

    /**
     * List active assignments of a route.
     */
    List<BusAssignment> findByRoute_RouteIdAndStatus(Long routeId, Status status);


    // -----------------------------------------------------
    // 🕒 DATE-WISE FILTERS
    // -----------------------------------------------------

    /**
     * Get all assignments for a bus on a specific date.
     */
    List<BusAssignment> findByBus_BusIdAndAssignmentDate(Long busId, LocalDate date);

    /**
     * Get all assignments for a driver on a specific date.
     */
    List<BusAssignment> findByDriver_DriverIdAndAssignmentDate(Long driverId, LocalDate date);


    // -----------------------------------------------------
    // 🛰 GPS ASSIGNMENT QUERIES
    // -----------------------------------------------------

    /**
     * Check if a GPS device is already assigned and active.
     */
    boolean existsByGpsDevice_GpsIdAndActiveFlag(Long gpsId, Integer activeFlag);

    /**
     * Find assignment by GPS device and active flag.
     */
    Optional<BusAssignment> findByGpsDevice_GpsIdAndActiveFlag(Long gpsId, Integer activeFlag);


    // -----------------------------------------------------
    // 🔍 SEARCH / LISTING HELPERS
    // -----------------------------------------------------

    /**
     * Get all active assignments (for dashboard).
     */
    List<BusAssignment> findByStatus(Status status);

    /**
     * List all assignments for a specific date (daily schedule).
     */
    List<BusAssignment> findByAssignmentDate(LocalDate date);

    /**
     * List assignments sorted by assignment date (newest first).
     */
    List<BusAssignment> findAllByOrderByAssignmentDateDesc();
}
