package com.cms.transport.driver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cms.transport.driver.model.QRScanLog;
import java.util.List;

@Repository
public interface QRScanLogRepository extends JpaRepository<QRScanLog, Long> {

    /**
     * Fetch all QR scan logs for a given driver, ordered by scan time descending.
     * Most recent scans appear first.
     */
	List<QRScanLog> findByDriver_DriverIdOrderByScannedAtDesc(Long driverId);

    /**
     * Fetch all QR scan logs with student, driver, bus, and route data eagerly loaded
     */
    @Query("SELECT q FROM QRScanLog q " +
           "LEFT JOIN FETCH q.student " +
           "LEFT JOIN FETCH q.driver " +
           "LEFT JOIN FETCH q.bus " +
           "LEFT JOIN FETCH q.route " +
           "ORDER BY q.scannedAt DESC")
    List<QRScanLog> findAllWithRelations();
}
