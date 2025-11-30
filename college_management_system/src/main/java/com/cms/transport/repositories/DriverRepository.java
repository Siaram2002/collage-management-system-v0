package com.cms.transport.repositories;

import com.cms.transport.enums.DriverStatus;
import com.cms.transport.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find driver using license number (unique field).
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    /**
     * Check if license number is already present.
     */
    boolean existsByLicenseNumber(String licenseNumber);


    // -----------------------------------------------------
    // 👤 FIND BY CONTACT OR USER ACCOUNT
    // -----------------------------------------------------

    /**
     * Find driver linked with a specific user account.
     */
    Optional<Driver> findByUserAccount_UserId(Long userId);

    /**
     * Find driver linked with a contact entry.
     */
    Optional<Driver> findByContact_ContactId(Long contactId);


    // -----------------------------------------------------
    // 🚦 DRIVER STATUS FILTERS
    // -----------------------------------------------------

    /**
     * Get drivers by current status (ACTIVE / INACTIVE / SUSPENDED).
     */
    List<Driver> findByStatus(DriverStatus status);

    /**
     * Get all active drivers sorted by name (useful for dropdown listing).
     */
    List<Driver> findByStatusOrderByFullNameAsc(DriverStatus status);


    // -----------------------------------------------------
    // 📅 LICENSE VALIDITY CHECK
    // -----------------------------------------------------

    /**
     * Find drivers whose license expires before/after a given date.
     * Useful for warnings & alerts.
     */
    List<Driver> findByLicenseExpiryDateBefore(LocalDate date);

    List<Driver> findByLicenseExpiryDateAfter(LocalDate date);

    /**
     * Find drivers whose license expires within a specific date range.
     */
    List<Driver> findByLicenseExpiryDateBetween(LocalDate from, LocalDate to);


    // -----------------------------------------------------
    // 🔍 SEARCH HELPERS
    // -----------------------------------------------------

    /**
     * Search drivers by matching part of full name (case-insensitive).
     * Supports UI searching.
     */
    List<Driver> findByFullNameContainingIgnoreCase(String name);


    // -----------------------------------------------------
    // 📊 SORTED LISTING
    // -----------------------------------------------------

    /**
     * Get all drivers sorted by creation date (newest first).
     */
    List<Driver> findAllByOrderByCreatedAtDesc();

    /**
     * List all drivers sorted alphabetically by name.
     */
    List<Driver> findAllByOrderByFullNameAsc();
}
