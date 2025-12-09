package com.cms.transport.bus.serviceImp;

import com.cms.transport.bus.dto.GPSLogDTO;
import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.bus.models.GPSLog;
import com.cms.transport.bus.repositories.GPSDeviceRepository;
import com.cms.transport.bus.repositories.GPSLogRepository;
import com.cms.transport.bus.service.GPSLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPSLogServiceImpl implements GPSLogService {

    private final GPSLogRepository gpsLogRepo;
    private final GPSDeviceRepository gpsDeviceRepo;

    @Override
    public GPSLog addLog(GPSLogDTO dto) {

        GPSDevice device = gpsDeviceRepo.findById(dto.getGpsId())
                .orElseThrow(() -> new RuntimeException("GPS Device not found"));

        GPSLog logEntry = GPSLog.builder()
                .gpsDevice(device)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .speed(dto.getSpeed())
                .heading(dto.getHeading())
                .accuracyMeters(dto.getAccuracyMeters())
                .ignitionOn(dto.getIgnitionOn())
                .batteryLevel(dto.getBatteryLevel())
                .recordedAt(dto.getRecordedAt() != null ? dto.getRecordedAt() : LocalDateTime.now())
                .build();

        log.info("Saving GPS log for device: {}", dto.getGpsId());
        return gpsLogRepo.save(logEntry);
    }
    
    
    
    /*
     * instead keep inserting new recording this below method just updates
     */
    
    
//    @Override
//    public GPSLog addLog(GPSLogDTO dto) {
//
//        GPSDevice device = gpsDeviceRepo.findById(dto.getGpsId())
//                .orElseThrow(() -> new RuntimeException("GPS Device not found"));
//
//        // 🔍 Fetch latest log for this device
//        GPSLog existingLog = gpsLogRepo
//                .findTopByGpsDevice_GpsIdOrderByRecordedAtDesc(dto.getGpsId())
//                .orElse(null);
//
//        if (existingLog != null) {
//            // 🔄 UPDATE existing log
//            existingLog.setLatitude(dto.getLatitude());
//            existingLog.setLongitude(dto.getLongitude());
//            existingLog.setSpeed(dto.getSpeed());
//            existingLog.setHeading(dto.getHeading());
//            existingLog.setAccuracyMeters(dto.getAccuracyMeters());
//            existingLog.setIgnitionOn(dto.getIgnitionOn());
//            existingLog.setBatteryLevel(dto.getBatteryLevel());
//            existingLog.setRecordedAt(dto.getRecordedAt() != null ? dto.getRecordedAt() : LocalDateTime.now());
//
//            log.info("Updating existing GPS log for device: {}", dto.getGpsId());
//            return gpsLogRepo.save(existingLog);
//        }
//
//        // 🆕 No log exists → create new one
//        GPSLog newLog = GPSLog.builder()
//                .gpsDevice(device)
//                .latitude(dto.getLatitude())
//                .longitude(dto.getLongitude())
//                .speed(dto.getSpeed())
//                .heading(dto.getHeading())
//                .accuracyMeters(dto.getAccuracyMeters())
//                .ignitionOn(dto.getIgnitionOn())
//                .batteryLevel(dto.getBatteryLevel())
//                .recordedAt(dto.getRecordedAt() != null ? dto.getRecordedAt() : LocalDateTime.now())
//                .build();
//
//        log.info("Saving NEW GPS log for device: {}", dto.getGpsId());
//        return gpsLogRepo.save(newLog);
//    }

    @Override
    public GPSLog getLatestLog(Long gpsId) {
        return gpsLogRepo.findTopByGpsDevice_GpsIdOrderByRecordedAtDesc(gpsId)
                .orElseThrow(() -> new RuntimeException("No logs found for device: " + gpsId));
    }

    @Override
    public List<GPSLog> getHistory(Long gpsId, LocalDateTime start, LocalDateTime end) {
        return gpsLogRepo.findByGpsDevice_GpsIdAndRecordedAtBetween(gpsId, start, end);
    }

    @Override
    public GPSLog saveLog(GPSLog gpsLog) {

        if (gpsLog == null) {
            throw new RuntimeException("GPSLog payload cannot be null");
        }

        if (gpsLog.getGpsDevice() == null || gpsLog.getGpsDevice().getGpsId() == null) {
            throw new RuntimeException("GPS Device is required for saving log");
        }

        // Ensure timestamp is always set
        if (gpsLog.getRecordedAt() == null) {
            gpsLog.setRecordedAt(LocalDateTime.now());
        }

        Long gpsId = gpsLog.getGpsDevice().getGpsId();
        log.info("Saving raw GPSLog entity for device: {}", gpsId);

        return gpsLogRepo.save(gpsLog);
    }

}
