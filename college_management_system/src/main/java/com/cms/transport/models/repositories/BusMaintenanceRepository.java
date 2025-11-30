package com.cms.transport.models.repositories;

import com.cms.transport.enums.MaintenanceType;
import com.cms.transport.models.BusMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusMaintenanceRepository extends JpaRepository<BusMaintenance, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find maintenance record for a bus on a specific start date.
     * Used to prevent duplicate maintenance entries for the same day.
     */
    Optional<BusMaintenance> findByBus_BusIdAndStartDate(Long busId, LocalDate startDate);


    /**
     * Check if a bus already has a maintenance record on given date.
     */
    boolean existsByBus_BusIdAndStartDate(Long busId, LocalDate startDate);


    // -----------------------------------------------------
    // 🚌 FILTER BY BUS
    // -----------------------------------------------------

    /**
     * Get all maintenance records for a specific bus.
     */
    List<BusMaintenance> findByBus_BusId(Long busId);

    /**
     * Get all maintenance records of a bus sorted by start date (newest first).
     */
    List<BusMaintenance> findByBus_BusIdOrderByStartDateDesc(Long busId);


    // -----------------------------------------------------
    // 🔧 FILTER BY MAINTENANCE TYPE
    // -----------------------------------------------------

    /**
     * Get maintenance tasks based on type (e.g., SERVICE, REPAIR).
     */
    List<BusMaintenance> findByMaintenanceType(MaintenanceType maintenanceType);


    // -----------------------------------------------------
    // 📅 DATE RANGE QUERIES
    // -----------------------------------------------------

    /**
     * Get all maintenance records starting on a specific date.
     */
    List<BusMaintenance> findByStartDate(LocalDate startDate);

    /**
     * Get all maintenance records between a date range.
     * Useful for monthly/weekly maintenance reports.
     */
    List<BusMaintenance> findByStartDateBetween(LocalDate from, LocalDate to);


    // -----------------------------------------------------
    // 📊 LISTING & REPORT USAGE
    // -----------------------------------------------------

    /**
     * List all maintenance entries sorted by createdAt (latest first).
     */
    List<BusMaintenance> findAllByOrderByCreatedAtDesc();

    /**
     * List upcoming maintenance (expectedEndDate >= today).
     */
    List<BusMaintenance> findByExpectedEndDateAfterOrderByExpectedEndDateAsc(LocalDate today);
}
