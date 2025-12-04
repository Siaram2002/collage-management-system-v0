package com.cms.transport.transportAssignment.repository;

import com.cms.transport.transportAssignment.model.TransportAssignment;
import com.cms.transport.transportAssignment.enums.Shift;
import com.cms.transport.bus.model.Bus;
import com.cms.transport.driver.model.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface TransportAssignmentRepository extends JpaRepository<TransportAssignment, Long> {

    // FIXED METHOD
    List<TransportAssignment> findByDriver_DriverId(Long driverId);

    // DRIVER CONFLICT
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
        FROM TransportAssignment a
        WHERE a.driver.driverId = :driverId
        AND a.shift = :shift
        AND a.status = com.cms.common.enums.Status.ACTIVE
        AND (
            (a.startDate <= :endDate AND a.endDate >= :startDate)
        )
        AND (:currentId IS NULL OR a.assignmentId <> :currentId)
    """)
    boolean driverConflict(@Param("driverId") Long driverId,
                           @Param("startDate") LocalDate start,
                           @Param("endDate") LocalDate end,
                           @Param("shift") Shift shift,
                           @Param("currentId") Long currentId);


    // BUS CONFLICT
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
        FROM TransportAssignment a
        WHERE a.bus.busId = :busId
        AND a.shift = :shift
        AND a.status = com.cms.common.enums.Status.ACTIVE
        AND (
            (a.startDate <= :endDate AND a.endDate >= :startDate)
        )
        AND (:currentId IS NULL OR a.assignmentId <> :currentId)
    """)
    boolean busConflict(@Param("busId") Long busId,
                        @Param("startDate") LocalDate start,
                        @Param("endDate") LocalDate end,
                        @Param("shift") Shift shift,
                        @Param("currentId") Long currentId);





}
