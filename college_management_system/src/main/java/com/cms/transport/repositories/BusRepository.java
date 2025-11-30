package com.cms.transport.repositories;

import com.cms.transport.enums.BusStatus;
import com.cms.transport.models.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    /**
     * Find a bus using its unique bus number.
     */
    Optional<Bus> findByBusNumber(String busNumber);

    /**
     * Check if a bus exists before creating one.
     */
    boolean existsByBusNumber(String busNumber);

    /**
     * Get all buses filtered by status (ACTIVE / INACTIVE).
     */
    List<Bus> findByStatus(BusStatus status);

    /**
     * Get buses by status sorted by busNumber (A-Z).
     */
    List<Bus> findByStatusOrderByBusNumberAsc(BusStatus status);

    /**
     * Find bus that has the given GPS device installed.
     */
    Optional<Bus> findByGpsDevice_GpsId(Long gpsId);

    /**
     * Check if a GPS device is already installed in a bus.
     */
    boolean existsByGpsDevice_GpsId(Long gpsId);

    /**
     * Find a bus using registration number (optional unique field).
     */
    Optional<Bus> findByRegistrationNumber(String registrationNumber);

    /**
     * Get all buses assigned to a specific contact person.
     */
    List<Bus> findByContact_ContactId(Long contactId);

    /**
     * Search buses by matching busNumber partially (case-insensitive).
     * Useful for frontend search bars.
     */
    List<Bus> findByBusNumberContainingIgnoreCase(String keyword);

    /**
     * List all buses ordered by creation time (newest first).
     */
    List<Bus> findAllByOrderByCreatedAtDesc();

    /**
     * List all buses sorted alphabetically by busNumber.
     */
    List<Bus> findAllByOrderByBusNumberAsc();
}
