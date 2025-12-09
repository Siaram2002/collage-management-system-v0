package com.cms.transport.bus.serviceImp;

import com.cms.transport.bus.dto.BusLocationDTO;

import com.cms.transport.bus.enums.BusStatus;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.bus.models.GPSLog;
import com.cms.transport.bus.repositories.BusRepository;
import com.cms.transport.bus.repositories.GPSDeviceRepository;
import com.cms.transport.bus.repositories.GPSLogRepository;
import com.cms.transport.bus.service.BusService;
import com.cms.transport.bus.service.GPSLogService;
import com.cms.transport.enums.TransportStatus;
import com.cms.transport.models.TransportAssignment;
import com.cms.transport.repositories.TransportAssignmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final GPSDeviceRepository gpsDeviceRepository;
   
    private final GPSLogService gpsLogService;
    private final GPSLogRepository gpsLogRepository; 
    
    private final TransportAssignmentRepository assignmentRepository;

    @Override
    public Bus createBus(Bus bus) {
        log.info("Creating bus: {}", bus.getBusNumber());
        return busRepository.save(bus);
    }

    @Override
    public Bus updateBus(Long busId, Bus updated) {
        log.info("Updating bus with ID: {}", busId);

        Bus existing = busRepository.findById(busId)
                .orElseThrow(() -> {
                    log.error("Bus not found with ID {}", busId);
                    return new RuntimeException("Bus not found");
                });

        if (updated.getBusNumber() != null) existing.setBusNumber(updated.getBusNumber());
        if (updated.getRegistrationNumber() != null) existing.setRegistrationNumber(updated.getRegistrationNumber());
        if (updated.getSeatingCapacity() != null) existing.setSeatingCapacity(updated.getSeatingCapacity());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
     

        log.info("Bus updated successfully: {}", busId);
        return busRepository.save(existing);
    }

    @Override
    public Bus getBus(Long busId) {
        log.info("Fetching bus with ID: {}", busId);

        return busRepository.findById(busId)
                .orElseThrow(() -> {
                    log.error("Bus not found with ID {}", busId);
                    return new RuntimeException("Bus not found");
                });
    }

    @Override
    public List<Bus> getAllBuses() {
        log.info("Fetching all buses");
        return busRepository.findAll();
    }

    @Override
    @Transactional
    public boolean deleteBus(Long busId) {

        log.info("Deleting bus with ID: {}", busId);

        Bus bus = busRepository.findById(busId)
                .orElse(null);

        if (bus == null) {
            log.warn("Bus not found for deletion: {}", busId);
            return false;
        }

        // 1️⃣ Check if bus is assigned to a transport assignment
        TransportAssignment assignment = bus.getAssignment();

        if (assignment != null && assignment.getStatus() == TransportStatus.ACTIVE) {

            log.info("Bus is currently assigned. Marking both bus and assignment as INACTIVE.");

            // Mark assignment inactive
            assignment.setStatus(TransportStatus.INACTIVE);

            // Mark bus inactive
            bus.setStatus(BusStatus.INACTIVE);

            // Save both
            assignmentRepository.save(assignment);
            busRepository.save(bus);

            return true; // Not deleted, but safely deactivated
        }

        // 2️⃣ If bus has no active assignment → safe to delete OR inactivate
        log.info("Bus has no active assignment. Making it INACTIVE.");

        bus.setStatus(BusStatus.INACTIVE);
        busRepository.save(bus);

        return true;
    }


    @Override
    public Bus assignGPS(Long busId, Long gpsId) {
        log.info("Assigning GPS {} to Bus {}", gpsId, busId);

        Bus bus = getBus(busId);

        GPSDevice device = gpsDeviceRepository.findById(gpsId)
                .orElseThrow(() -> {
                    log.error("GPS device not found: {}", gpsId);
                    return new RuntimeException("GPS not found");
                });

        if (device.getBus() != null && !device.getBus().getBusId().equals(busId)) {
            log.error("GPS device {} already assigned to another bus", gpsId);
            throw new RuntimeException("GPS device already assigned to another bus");
        }

        bus.setGpsDevice(device);

        log.info("GPS {} assigned to Bus {}", gpsId, busId);
        return busRepository.save(bus);
    }

    @Override
    public Bus removeGPS(Long busId) {
        log.info("Removing GPS from Bus {}", busId);

        Bus bus = getBus(busId);
        bus.setGpsDevice(null);

        log.info("GPS removed from Bus {}", busId);
        return busRepository.save(bus);
    }
    
    
    @Override
    public BusLocationDTO getLatestBusLocation(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + busId));

        GPSDevice device = bus.getGpsDevice();
        if (device == null) {
            throw new RuntimeException("Bus does not have a GPS device assigned");
        }

        GPSLog latestLog = gpsLogService.getLatestLog(device.getGpsId());

        BusLocationDTO dto = new BusLocationDTO();
        dto.setBusId(bus.getBusId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setLatitude(latestLog.getLatitude());
        dto.setLongitude(latestLog.getLongitude());
        dto.setSpeed(latestLog.getSpeed());
        dto.setHeading(latestLog.getHeading());
        dto.setRecordedAt(latestLog.getRecordedAt());

        return dto;
    }
    
 
  // inject repository instance

    @Override
    public List<BusLocationDTO> getAllBusesLatestLocations() {
        List<Bus> buses = busRepository.findAll();
        List<BusLocationDTO> locations = new ArrayList<>();

        for (Bus bus : buses) {
            if (bus.getGpsDevice() != null) {
                // Use the injected instance, not the class
                gpsLogRepository.findTopByGpsDevice_GpsIdOrderByRecordedAtDesc(bus.getGpsDevice().getGpsId())
                        .ifPresent(log -> {
                            locations.add(BusLocationDTO.builder()
                                    .busId(bus.getBusId())
                                    .busNumber(bus.getBusNumber())
                                    .latitude(log.getLatitude())
                                    .longitude(log.getLongitude())
                                    .speed(log.getSpeed())
                                    .heading(log.getHeading())
                                    .recordedAt(log.getRecordedAt())
                                    .build());
                        });
            }
        }

        return locations;
    }

}
