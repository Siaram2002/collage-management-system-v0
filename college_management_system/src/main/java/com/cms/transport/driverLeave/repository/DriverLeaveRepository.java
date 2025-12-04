package com.cms.transport.driverLeave.repository;

import com.cms.transport.driver.model.Driver;
import com.cms.transport.driverLeave.enums.ApprovalStatus;
import com.cms.transport.driverLeave.model.DriverLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DriverLeaveRepository extends JpaRepository<DriverLeave, Long> {

    // check if driver has APPROVED leave covering the date
//    boolean existsByDriverAndApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
//            Driver driver, ApprovalStatus approvalStatus, LocalDate start, LocalDate end);

    // check any leave for driver overlapping date
    boolean existsByDriverAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Driver driver, LocalDate start, LocalDate end);

    List<DriverLeave> findByDriver(Driver driver);

    List<DriverLeave> findByApprovalStatus(ApprovalStatus status);



    boolean existsByDriverAndApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Driver driver, ApprovalStatus approvalStatus, LocalDate endDate, LocalDate startDate);

    @Query("""
    SELECT dl FROM DriverLeave dl
    WHERE dl.driver.driverId = :driverId
      AND dl.approvalStatus = 'APPROVED'
      AND dl.status = 'ACTIVE'
      AND (dl.startDate <= :endDate AND dl.endDate >= :startDate)
    """)
    List<DriverLeave> findLeaveConflicts(
            Long driverId, LocalDate startDate, LocalDate endDate);



    @Query("""
        SELECT dl FROM DriverLeave dl
        WHERE dl.driver.driverId = :driverId
        AND dl.approvalStatus = com.cms.transport.driverLeave.enums.ApprovalStatus.APPROVED
        AND dl.status = com.cms.common.enums.Status.ACTIVE
        AND dl.startDate <= :endDate
        AND dl.endDate >= :startDate
    """)
    List<DriverLeave> findOverlappingLeaves(Long driverId, LocalDate startDate, LocalDate endDate);



    @Query("""
    SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
    FROM DriverLeave l
    WHERE l.driver.driverId = :driverId
    AND l.approvalStatus = com.cms.transport.driverLeave.enums.ApprovalStatus.APPROVED
    AND (
        l.startDate <= :endDate AND l.endDate >= :startDate
    )
""")
    boolean isDriverOnLeave(Long driverId, LocalDate startDate, LocalDate endDate);

}

