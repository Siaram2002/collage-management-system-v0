package com.cms.transport.bus.serviceImp;

import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.bus.repositories.GPSDeviceRepository;
import com.cms.transport.bus.service.GPSDeviceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GPSDeviceServiceImpl implements GPSDeviceService {

    private final GPSDeviceRepository gpsRepo;

    @Override
    public GPSDevice registerDevice(GPSDevice device) {
        log.info("Registering GPS device: {}", device.getDeviceSerialNumber());
        return gpsRepo.save(device);
    }

    @Override
    public GPSDevice updateDevice(Long id, GPSDevice updated) {
        log.info("Updating GPS device ID: {}", id);

        GPSDevice existing = gpsRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("GPS device not found: {}", id);
                    return new RuntimeException("GPS device not found");
                });

        if (updated.getDeviceSerialNumber() != null)
            existing.setDeviceSerialNumber(updated.getDeviceSerialNumber());
        if (updated.getProvider() != null)
            existing.setProvider(updated.getProvider());
        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());

        log.info("GPS device updated successfully: {}", id);
        return gpsRepo.save(existing);
    }

    @Override
    public GPSDevice getDevice(Long id) {
        log.info("Fetching GPS device ID: {}", id);

        return gpsRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("GPS device not found: {}", id);
                    return new RuntimeException("GPS device not found");
                });
    }

    @Override
    public List<GPSDevice> getAllDevices() {
        log.info("Fetching all GPS devices");
        return gpsRepo.findAll();
    }

    @Override
    public boolean deleteDevice(Long id) {
        log.info("Deleting GPS device ID: {}", id);

        if (!gpsRepo.existsById(id)) {
            log.warn("GPS device not found for deletion: {}", id);
            return false;
        }

        gpsRepo.deleteById(id);
        log.info("GPS device deleted successfully: {}", id);
        return true;
    }

    @Override
    public GPSDevice updateHealth(Long id, Integer battery, Boolean ignition, String pingTime) {
        log.info("Updating GPS health for device ID: {}", id);

        GPSDevice device = getDevice(id);

        if (battery != null) device.setBatteryLevel(battery);
        if (ignition != null) device.setIgnitionOn(ignition);
        if (pingTime != null) device.setLastPingAt(LocalDateTime.parse(pingTime));

        log.info("GPS health updated for device ID: {}", id);
        return gpsRepo.save(device);
    }
}
