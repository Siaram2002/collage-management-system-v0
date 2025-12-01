package com.cms.transport.repositories;

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

    Optional<BusAssignment> findByBus_BusIdAndAssignmentDateAndActiveFlag(
            Long busId, LocalDate assignmentDate, Integer activeFlag);

    Optional<BusAssignment> findByDriver_DriverIdAndAssignmentDateAndActiveFlag(
            Long driverId, LocalDate assignmentDate, Integer activeFlag);

    // -----------------------------------------------------
    // 🚍 QUERY BY BUS / DRIVER / ROUTE
    // -----------------------------------------------------

    List<BusAssignment> findByBus_BusIdAndStatus(Long busId, Status status);

    List<BusAssignment> findByDriver_DriverIdAndStatus(Long driverId, Status status);

    List<BusAssignment> findByRoute_RouteIdAndStatus(Long routeId, Status status);

    // -----------------------------------------------------
    // 🕒 DATE-WISE FILTERS
    // -----------------------------------------------------

    List<BusAssignment> findByBus_BusIdAndAssignmentDate(Long busId, LocalDate date);

    List<BusAssignment> findByDriver_DriverIdAndAssignmentDate(Long driverId, LocalDate date);

    // -----------------------------------------------------
    // 🛰 GPS ASSIGNMENT QUERIES
    // -----------------------------------------------------

    boolean existsByGpsDevice_GpsIdAndActiveFlag(Long gpsId, Integer activeFlag);

    Optional<BusAssignment> findByGpsDevice_GpsIdAndActiveFlag(Long gpsId, Integer activeFlag);

    // -----------------------------------------------------
    // 🔍 SEARCH / LISTING HELPERS
    // -----------------------------------------------------

    List<BusAssignment> findByStatus(Status status);

    List<BusAssignment> findByAssignmentDate(LocalDate date);

    List<BusAssignment> findAllByOrderByAssignmentDateDesc();

    List<BusAssignment> findAllByOrderByAssignmentDateAsc(); // optional for scheduling
}
