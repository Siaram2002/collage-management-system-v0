package com.cms.transport.GPSDevice.serviceImpl;

import com.cms.transport.GPSDevice.dto.GPSDeviceRequest;
import com.cms.transport.GPSDevice.dto.GPSDeviceResponse;
import com.cms.transport.GPSDevice.dto.GPSPingUpdateRequest;
import com.cms.transport.GPSDevice.enums.GPSStatus;
import com.cms.transport.GPSDevice.models.GPSDevice;
import com.cms.transport.GPSDevice.repository.GPSDeviceRepository;
import com.cms.transport.GPSDevice.service.GPSDeviceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GPSDeviceServiceImpl implements GPSDeviceService {

    private final GPSDeviceRepository gpsRepo;

    @Override
    public GPSDeviceResponse register(GPSDeviceRequest dto) {

        if (gpsRepo.existsByDeviceSerialNumber(dto.getDeviceSerialNumber())) {
            throw new RuntimeException("GPS Serial Number already exists!");
        }

        GPSDevice gps = GPSDevice.builder()
                .deviceSerialNumber(dto.getDeviceSerialNumber())
                .provider(dto.getProvider())
                .status(dto.getStatus())
                .batteryLevel(dto.getBatteryLevel())
                .ignitionOn(dto.getIgnitionOn())
                .lastPingAt(LocalDateTime.now())
                .build();

        gpsRepo.save(gps);

        return toResponse(gps);
    }

    @Override
    public GPSDeviceResponse update(Long gpsId, GPSDeviceRequest dto) {

        GPSDevice gps = gpsRepo.findById(gpsId)
                .orElseThrow(() -> new RuntimeException("GPS Device not found"));

        gps.setProvider(dto.getProvider());
        gps.setStatus(dto.getStatus());
        gps.setBatteryLevel(dto.getBatteryLevel());
        gps.setIgnitionOn(dto.getIgnitionOn());

        gpsRepo.save(gps);

        return toResponse(gps);
    }

    @Override
    public GPSDeviceResponse getById(Long gpsId) {
        GPSDevice gps = gpsRepo.findById(gpsId)
                .orElseThrow(() -> new RuntimeException("GPS Device not found"));

        return toResponse(gps);
    }

    @Override
    public List<GPSDeviceResponse> getAll() {
        return gpsRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long gpsId) {
        GPSDevice gps = gpsRepo.findById(gpsId)
                .orElseThrow(() -> new RuntimeException("GPS Device not found"));

        // Cannot delete if assigned or installed
        if (gps.getStatus() == GPSStatus.ASSIGNED || gps.getStatus() == GPSStatus.INSTALLED) {
            throw new RuntimeException("Cannot delete a device that is assigned/installed");
        }

        gpsRepo.delete(gps);
    }

    @Override
    public GPSDeviceResponse updatePing(Long gpsId, GPSPingUpdateRequest dto) {

        GPSDevice gps = gpsRepo.findById(gpsId)
                .orElseThrow(() -> new RuntimeException("GPS Device not found"));

        gps.setBatteryLevel(dto.getBatteryLevel());
        gps.setIgnitionOn(dto.getIgnitionOn());
        gps.setLastPingAt(LocalDateTime.now());

        gpsRepo.save(gps);

        return toResponse(gps);
    }


    // -----------------------------
    // Mapper
    // -----------------------------
    private GPSDeviceResponse toResponse(GPSDevice gps) {
        return GPSDeviceResponse.builder()
                .gpsId(gps.getGpsId())
                .deviceSerialNumber(gps.getDeviceSerialNumber())
                .provider(gps.getProvider())
                .status(gps.getStatus())
                .batteryLevel(gps.getBatteryLevel())
                .ignitionOn(gps.getIgnitionOn())
                .lastPingAt(gps.getLastPingAt())
                .createdAt(gps.getCreatedAt())
                .updatedAt(gps.getUpdatedAt())
                .build();
    }
}
