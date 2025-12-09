package com.cms.transport.bus.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cms.transport.bus.models.GPSLog;

@Repository
public interface GPSLogRepository extends JpaRepository<GPSLog, Long> {

    // latest log for device
    Optional<GPSLog> findTopByGpsDevice_GpsIdOrderByRecordedAtDesc(Long gpsId);
    
    

    // logs for device between time range
 //   List<GPSLog> findByGpsDevice_GpsIdAndRecordedAtBetween(Long gpsId, LocalDateTime start, LocalDateTime end);
    /**
     * Fetch the latest GPS log for a given GPS device.
     */


    /**
     * Fetch all GPS logs for a device between two timestamps.
     */
    List<GPSLog> findByGpsDevice_GpsIdAndRecordedAtBetween(Long gpsId, LocalDateTime start, LocalDateTime end);

}
