package com.cms.transport.bus.repositories;

import com.cms.transport.bus.enums.BusStatus;
import com.cms.transport.bus.models.Bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByBusNumber(String busNumber);
    boolean existsByBusNumber(String busNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findByStatusOrderByBusNumberAsc(BusStatus status);
    Optional<Bus> findByGpsDevice_GpsId(Long gpsId);
    boolean existsByGpsDevice_GpsId(Long gpsId);
    Optional<Bus> findByRegistrationNumber(String registrationNumber);



    List<Bus> findByBusNumberContainingIgnoreCase(String keyword);
    List<Bus> findAllByOrderByCreatedAtDesc();
    List<Bus> findAllByOrderByBusNumberAsc();
}
