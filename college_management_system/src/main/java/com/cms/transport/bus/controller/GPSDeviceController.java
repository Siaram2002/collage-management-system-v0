package com.cms.transport.bus.controller;

import com.cms.transport.bus.models.GPSDevice;
import com.cms.transport.bus.service.GPSDeviceService;
import com.cms.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gps-devices")
@RequiredArgsConstructor
public class GPSDeviceController {

    private final GPSDeviceService gpsService;

    @PostMapping
    public ResponseEntity<ApiResponse> registerDevice(@RequestBody GPSDevice device) {
        log.info("Registering new GPS device: {}", device);
        GPSDevice saved = gpsService.registerDevice(device);
        return ResponseEntity.ok(ApiResponse.success("GPS Device registered", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDevice(@PathVariable Long id, @RequestBody GPSDevice device) {
        log.info("Updating GPS Device {}", id);
        GPSDevice updated = gpsService.updateDevice(id, device);
        return ResponseEntity.ok(ApiResponse.success("GPS Device updated", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDevice(@PathVariable Long id) {
        log.info("Fetching GPS device {}", id);
        GPSDevice device = gpsService.getDevice(id);
        return ResponseEntity.ok(ApiResponse.success("GPS Device fetched", device));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllDevices() {
        log.info("Fetching all GPS devices");
        List<GPSDevice> devices = gpsService.getAllDevices();
        return ResponseEntity.ok(ApiResponse.success("All GPS devices fetched", devices));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDevice(@PathVariable Long id) {
        log.warn("Deleting GPS device {}", id);
        boolean deleted = gpsService.deleteDevice(id);
        if (!deleted) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("GPS device not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("GPS Device deleted"));
    }

    @PatchMapping("/{id}/health")
    public ResponseEntity<ApiResponse> updateHealth(
            @PathVariable Long id,
            @RequestParam Integer battery,
            @RequestParam Boolean ignition,
            @RequestParam String pingTime
    ) {
        log.info("Updating device health for GPS {} | battery={} ignition={}", id, battery, ignition);
        GPSDevice device = gpsService.updateHealth(id, battery, ignition, pingTime);
        return ResponseEntity.ok(ApiResponse.success("GPS health updated", device));
    }
}
