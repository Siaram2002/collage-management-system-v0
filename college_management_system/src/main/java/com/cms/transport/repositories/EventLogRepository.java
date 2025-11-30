package com.cms.transport.repositories;

import com.cms.transport.models.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FILTERS
    // -----------------------------------------------------

    /**
     * Get logs for a specific entity such as:
     * BusAssignment / Driver / GPSLog etc.
     */
    List<EventLog> findByEntityName(String entityName);

    /**
     * Get logs for a specific entity record
     * (example: logs for assignmentId = X or driverId = Y).
     */
    List<EventLog> findByEntityId(Long entityId);


    // -----------------------------------------------------
    // 🎚 EVENT TYPE FILTER
    // -----------------------------------------------------

    /**
     * Filter logs by event type:
     * ASSIGNMENT_CREATED / DRIVER_LEAVE / FIELD_UPDATED ...
     */
    List<EventLog> findByEventType(String eventType);


    // -----------------------------------------------------
    // 📑 FIELD-LEVEL FILTER
    // -----------------------------------------------------

    /**
     * Get logs where a specific field changed.
     */
    List<EventLog> findByFieldName(String fieldName);


    // -----------------------------------------------------
    // 👤 PERFORMED BY FILTER
    // -----------------------------------------------------

    /**
     * Get logs performed by specific user / admin / system.
     */
    List<EventLog> findByPerformedBy(String performedBy);


    // -----------------------------------------------------
    // 🔍 SEARCH HELPERS
    // -----------------------------------------------------

    /**
     * Search logs containing specific text inside details (case-insensitive).
     * Useful for searching: "GPS", "Trip 14", "Failed request" etc.
     */
    List<EventLog> findByDetailsContainingIgnoreCase(String keyword);


    // -----------------------------------------------------
    // 📊 SORTING
    // -----------------------------------------------------

    /**
     * Get all logs sorted by timestamp descending (newest first).
     */
    List<EventLog> findAllByOrderByTimestampDesc();

    /**
     * Get entity-specific logs sorted newest first.
     */
    List<EventLog> findByEntityNameOrderByTimestampDesc(String entityName);
}
