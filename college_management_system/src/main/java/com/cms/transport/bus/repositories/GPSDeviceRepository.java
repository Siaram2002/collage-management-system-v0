package com.cms.transport.bus.repositories;

import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.enums.GPSStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GPSDeviceRepository extends JpaRepository<GPSDevice, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find a GPS device using its unique serial number.
     */
    Optional<GPSDevice> findByDeviceSerialNumber(String serialNumber);

    /**
     * Check if a device exists with a given serial number.
     */
    boolean existsByDeviceSerialNumber(String serialNumber);


    // -----------------------------------------------------
    // 🚦 STATUS FILTERS (ACTIVE / INACTIVE / FAULTY)
    // -----------------------------------------------------

    /**
     * Get all devices by status.
     */
    List<GPSDevice> findByStatus(GPSStatus status);

    /**
     * Get all active devices sorted by provider name.
     */
    List<GPSDevice> findByStatusOrderByProviderAsc(GPSStatus status);


    // -----------------------------------------------------
    // 📡 HEALTH & TELEMETRY FILTERS
    // -----------------------------------------------------

    /**
     * Find devices whose last ping was before a given timestamp
     * (used for "offline device" alerts).
     */
    List<GPSDevice> findByLastPingAtBefore(LocalDateTime timestamp);

    /**
     * Find devices that are currently sending recent pings.
     */
    List<GPSDevice> findByLastPingAtAfter(LocalDateTime timestamp);


    // -----------------------------------------------------
    // 🔋 BATTERY HEALTH FILTERS
    // -----------------------------------------------------

    /**
     * Find devices with battery percentage below a threshold.
     */
    List<GPSDevice> findByBatteryLevelLessThan(Integer level);

    /**
     * Find devices with battery percentage above a threshold.
     */
    List<GPSDevice> findByBatteryLevelGreaterThan(Integer level);


    // -----------------------------------------------------
    // 🔍 SEARCH HELPERS
    // -----------------------------------------------------

    /**
     * Search devices where provider name contains text (case-insensitive).
     */
    List<GPSDevice> findByProviderContainingIgnoreCase(String provider);


    // -----------------------------------------------------
    // 📊 SORTED LISTING
    // -----------------------------------------------------

    /**
     * Get all devices sorted by creation date (newest first).
     */
    List<GPSDevice> findAllByOrderByCreatedAtDesc();

    /**
     * List all devices alphabetically by provider.
     */
    List<GPSDevice> findAllByOrderByProviderAsc();
}
