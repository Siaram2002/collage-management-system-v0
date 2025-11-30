package com.cms.transport.repositories;

import com.cms.transport.models.TripSchedule;
import com.cms.transport.models.BusAssignment;
import com.cms.transport.enums.TripType;
import com.cms.transport.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripScheduleRepository extends JpaRepository<TripSchedule, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Get all trips for a specific bus assignment.
     */
    List<TripSchedule> findByBusAssignment(BusAssignment assignment);

    /**
     * Find trips by trip type (MORNING / EVENING).
     */
    List<TripSchedule> findByTripType(TripType tripType);

    /**
     * Find trips by status (PLANNED / ONGOING / COMPLETED / CANCELLED).
     */
    List<TripSchedule> findByTripStatus(TripStatus tripStatus);


    // -----------------------------------------------------
    // ⏱ TIME RANGE FILTERS
    // -----------------------------------------------------

    /**
     * Find trips that start within a specific planned time range.
     */
    List<TripSchedule> findByPlannedStartBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find trips that actually started within a specific time range.
     */
    List<TripSchedule> findByActualStartBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find trips that ended within a specific actual end time range.
     */
    List<TripSchedule> findByActualEndBetween(LocalDateTime start, LocalDateTime end);


    // -----------------------------------------------------
    // 📊 SORTING HELPERS
    // -----------------------------------------------------

    /**
     * Get all trips for an assignment ordered by planned start ascending.
     */
    List<TripSchedule> findByBusAssignmentOrderByPlannedStartAsc(BusAssignment assignment);

    /**
     * Get all trips for an assignment ordered by planned start descending.
     */
    List<TripSchedule> findByBusAssignmentOrderByPlannedStartDesc(BusAssignment assignment);
}
