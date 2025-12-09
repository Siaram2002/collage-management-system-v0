package com.cms.transport.bus.controller;

import com.cms.transport.bus.dto.BusLocationDTO;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.bus.service.BusService;
import com.cms.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    public ResponseEntity<ApiResponse> createBus(@RequestBody Bus bus) {
        log.info("Creating bus: {}", bus);
        Bus saved = busService.createBus(bus);
        return ResponseEntity.ok(ApiResponse.success("Bus created successfully", saved));
    }

    @PutMapping("/update/{busId}")
    public ResponseEntity<ApiResponse> updateBus(@PathVariable Long busId, @RequestBody Bus bus) {
        log.info("Updating bus with ID: {}", busId);
        Bus updated = busService.updateBus(busId, bus);
        return ResponseEntity.ok(ApiResponse.success("Bus updated successfully", updated));
    }

    @GetMapping("/{busId}")
    public ResponseEntity<ApiResponse> getBus(@PathVariable Long busId) {
        log.info("Fetching bus with ID: {}", busId);
        Bus bus = busService.getBus(busId);
        return ResponseEntity.ok(ApiResponse.success("Bus fetched", bus));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllBuses() {
        log.info("Fetching all buses");
        List<Bus> buses = busService.getAllBuses();
        return ResponseEntity.ok(ApiResponse.success("All buses fetched", buses));
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<ApiResponse> deleteBus(@PathVariable Long busId) {
        log.warn("Deleting bus with ID: {}", busId);
        boolean deleted = busService.deleteBus(busId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Bus not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Bus deleted successfully"));
    }

    @PatchMapping("/{busId}/assign-gps/{gpsId}")
    public ResponseEntity<ApiResponse> assignGPS(@PathVariable Long busId, @PathVariable Long gpsId) {
        log.info("Assigning GPS {} to Bus {}", gpsId, busId);
        Bus bus = busService.assignGPS(busId, gpsId);
        return ResponseEntity.ok(ApiResponse.success("GPS assigned successfully", bus));
    }

    @PatchMapping("/{busId}/remove-gps")
    public ResponseEntity<ApiResponse> removeGPS(@PathVariable Long busId) {
        log.info("Removing GPS from Bus {}", busId);
        Bus bus = busService.removeGPS(busId);
        return ResponseEntity.ok(ApiResponse.success("GPS removed successfully", bus));
    }
    
    /**
     * Get latest location of a bus
     */
    @GetMapping("/{busId}/location")
    public ResponseEntity<ApiResponse> getLatestLocation(@PathVariable Long busId) {
        log.info("Fetching latest location for busId={}", busId); // log the request
        try {
            BusLocationDTO location = busService.getLatestBusLocation(busId);
            log.info("Successfully fetched location for busId={}: {}", busId, location);
            return ResponseEntity.ok(ApiResponse.success("Latest bus location fetched successfully", location));
        } catch (Exception e) {
            log.error("Error fetching location for busId={}", busId, e); // log the error with stacktrace
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }
    
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse> getAllBusesLatestLocations() {
        try {
            List<BusLocationDTO> locations = busService.getAllBusesLatestLocations();
            return ResponseEntity.ok(ApiResponse.success("All buses latest locations fetched", locations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

}
