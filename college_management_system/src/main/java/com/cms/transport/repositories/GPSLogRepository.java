package com.cms.transport.repositories;

import com.cms.transport.models.GPSLog;
import com.cms.transport.models.GPSDevice;
import com.cms.transport.models.BusAssignment;
import com.cms.transport.models.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GPSLogRepository extends JpaRepository<GPSLog, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find logs for a specific GPS device.
     */
    List<GPSLog> findByGpsDevice(GPSDevice gpsDevice);


    // -----------------------------------------------------
    // ⏱ TIME RANGE FILTERS
    // -----------------------------------------------------

    /**
     * Get logs recorded before a specific timestamp.
     */
    List<GPSLog> findByRecordedAtBefore(LocalDateTime timestamp);

    /**
     * Get logs recorded after a specific timestamp.
     */
    List<GPSLog> findByRecordedAtAfter(LocalDateTime timestamp);

    /**
     * Get logs recorded within a specific range.
     */
    List<GPSLog> findByRecordedAtBetween(LocalDateTime start, LocalDateTime end);


    // -----------------------------------------------------
    // 🚌 ASSIGNMENT-BASED QUERIES
    // -----------------------------------------------------

    /**
     * Get logs linked to a specific bus assignment.
     */
    List<GPSLog> findByAssignment(BusAssignment assignment);

    /**
     * Get logs for an assignment within a time range.
     */
    List<GPSLog> findByAssignmentAndRecordedAtBetween(
            BusAssignment assignment,
            LocalDateTime start,
            LocalDateTime end
    );


    // -----------------------------------------------------
    // 🚐 TRIP-SCHEDULE QUERIES
    // -----------------------------------------------------

    /**
     * Get logs linked to a specific trip.
     */
    List<GPSLog> findByTripSchedule(TripSchedule tripSchedule);

    /**
     * Get trip logs within time window.
     */
    List<GPSLog> findByTripScheduleAndRecordedAtBetween(
            TripSchedule tripSchedule,
            LocalDateTime start,
            LocalDateTime end
    );


    // -----------------------------------------------------
    // 📊 SORTING HELPERS
    // -----------------------------------------------------

    /**
     * Get all logs for a device ordered by recorded time (oldest first).
     */
    List<GPSLog> findByGpsDeviceOrderByRecordedAtAsc(GPSDevice gpsDevice);

    /**
     * Get logs ordered by server received time (newest first).
     */
    List<GPSLog> findAllByOrderByServerReceivedAtDesc();
}
