package com.cms.transport.GPSDevice.controller;

import com.cms.transport.GPSDevice.dto.GPSDeviceRequest;
import com.cms.transport.GPSDevice.dto.GPSDeviceResponse;
import com.cms.transport.GPSDevice.dto.GPSPingUpdateRequest;
import com.cms.transport.GPSDevice.service.GPSDeviceService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gps-devices")
@RequiredArgsConstructor
public class GPSDeviceController {

    private final GPSDeviceService gpsService;

    // Register new device
    @PostMapping
    public ResponseEntity<GPSDeviceResponse> register(@RequestBody GPSDeviceRequest dto) {
        return ResponseEntity.ok(gpsService.register(dto));
    }

    // Update device
    @PutMapping("/{gpsId}")
    public ResponseEntity<GPSDeviceResponse> update(
            @PathVariable Long gpsId,
            @RequestBody GPSDeviceRequest dto) {

        return ResponseEntity.ok(gpsService.update(gpsId, dto));
    }

    // Get by ID
    @GetMapping("/{gpsId}")
    public ResponseEntity<GPSDeviceResponse> getById(@PathVariable Long gpsId) {
        return ResponseEntity.ok(gpsService.getById(gpsId));
    }

    // Get all devices
    @GetMapping
    public ResponseEntity<List<GPSDeviceResponse>> getAll() {
        return ResponseEntity.ok(gpsService.getAll());
    }

    // Delete
    @DeleteMapping("/{gpsId}")
    public ResponseEntity<String> delete(@PathVariable Long gpsId) {
        gpsService.delete(gpsId);
        return ResponseEntity.ok("GPS Device deleted successfully");
    }

    // Update telemetry (last ping)
    @PatchMapping("/{gpsId}/ping")
    public ResponseEntity<GPSDeviceResponse> updatePing(
            @PathVariable Long gpsId,
            @RequestBody GPSPingUpdateRequest dto) {

        return ResponseEntity.ok(gpsService.updatePing(gpsId, dto));
    }
}
