package com.cms.transport.busMaintenance.repository;

import com.cms.transport.busMaintenance.model.BusMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusMaintenanceRepository extends JpaRepository<BusMaintenance, Long> {

    List<BusMaintenance> findByBusBusId(Long busId);

    // Check if bus is under maintenance in given date range
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM BusMaintenance m " +
            "WHERE m.bus.busId = :busId " +
            "AND (m.startDate <= :endDate AND m.expectedEndDate >= :startDate)")
    boolean existsByBusAndDateRange(@Param("busId") Long busId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
