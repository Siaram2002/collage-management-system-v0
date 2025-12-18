package com.cms.transport.repositories;

import com.cms.transport.models.TransportAssignment;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.route.models.Route;
import com.cms.transport.enums.TransportStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface TransportAssignmentRepository extends JpaRepository<TransportAssignment, Long> {

    // ============================================================
    // Check if a specific bus, driver, route, and date assignment exists
    // ============================================================
    boolean existsByBusAndDriverAndRouteAndAssignmentDate(
            Bus bus, Driver driver, Route route, LocalDate assignmentDate);

    // ============================================================
    // Find active assignment by driver
    // ============================================================
    Optional<TransportAssignment> findByDriverAndStatus(Driver driver, TransportStatus status);

    // ============================================================
    // Find active assignment by bus
    // ============================================================
    Optional<TransportAssignment> findByBusAndStatus(Bus bus, TransportStatus status);
    
    
    boolean existsByDriver_DriverIdAndStatus(Long driverId, TransportStatus status);


    // ============================================================
    // Optional: Find all assignments for a specific route and date
    // Useful if multiple buses serve same route
    // ============================================================
    List<TransportAssignment> findByRouteAndAssignmentDate(Route route, LocalDate assignmentDate);
    
    List<TransportAssignment> findByRouteRouteId(Long routeId);

    /**
     * Fetch all assignments with driver, bus, and route data eagerly loaded
     */
//    @Query("SELECT a FROM TransportAssignment a " +
//           "LEFT JOIN FETCH a.driver " +
//           "LEFT JOIN FETCH a.bus " +
//           "LEFT JOIN FETCH a.route " +
//           "ORDER BY a.createdAt DESC")
//    List<TransportAssignment> findAllWithRelations();

    // ============================================================
    // Fetch ALL assignments with relations (TABLE VIEW API)
    // ============================================================
    @Query("""
        SELECT a FROM TransportAssignment a
        LEFT JOIN FETCH a.driver
        LEFT JOIN FETCH a.bus
        LEFT JOIN FETCH a.route
        ORDER BY a.createdAt DESC
    """)
    List<TransportAssignment> findAllWithRelations();

}
