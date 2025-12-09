package com.cms.transport.driver.repository;

import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.driver.model.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;





@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // -------------------------------
    // BASIC FINDERS
    // -------------------------------

    Optional<Driver> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);

    // -------------------------------
    // FIND BY LINKED ENTITIES
    // -------------------------------

    // UserAccount relation (matches User.userId)
    Optional<Driver> findByUserAccount_UserId(Long userId);

    // Contact relation (matches Contact.id)
    Optional<Driver> findByContact_Id(Long contactId);

    // -------------------------------
    // DRIVER STATUS FILTERS
    // -------------------------------

    List<Driver> findByStatus(DriverStatus status);
    List<Driver> findByStatusOrderByFullNameAsc(DriverStatus status);

    // -------------------------------
    // LICENSE VALIDITY CHECK
    // -------------------------------

    List<Driver> findByLicenseExpiryDateBefore(LocalDate date);
    List<Driver> findByLicenseExpiryDateAfter(LocalDate date);
    List<Driver> findByLicenseExpiryDateBetween(LocalDate from, LocalDate to);

    // -------------------------------
    // SEARCH HELPERS
    // -------------------------------

    List<Driver> findByFullNameContainingIgnoreCase(String name);

    // -------------------------------
    // SORTED LISTING
    // -------------------------------

    List<Driver> findAllByOrderByCreatedAtDesc();
    List<Driver> findAllByOrderByFullNameAsc();
    
    List<Driver> findByFullNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(String name, String license);



    /**
     * Find a driver by their unique license number
     * @param licenseNumber license number of the driver
     * @return Optional containing the driver if found
     */
//    Optional<Driver> findByLicenseNumber(String licenseNumber);
//
//    /**
//     * Find all drivers by their current status (ACTIVE / INACTIVE)
//     * @param status DriverStatus enum
//     * @return List of drivers with the given status
//     */
//    List<Driver> findByStatus(DriverStatus status);
//
//
//
//
//    // Check if a driver with given license exists
//    boolean existsByLicenseNumber(String licenseNumber);

}
