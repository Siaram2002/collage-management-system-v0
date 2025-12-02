package com.cms.transport.driver.controller;

import com.cms.common.ApiResponse;
import com.cms.common.exceptions.ResourceNotFoundException;
import com.cms.transport.driver.dto.ScanResultDTO;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.awt.PageAttributes.MediaType;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // -------------------------------------------------------------------------
    // Register a new driver (with optional photo)
    // -------------------------------------------------------------------------
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<Driver> createDriver(
            @RequestPart("driver") String driverJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Driver driver = mapper.readValue(driverJson, Driver.class);

        Driver createdDriver = driverService.createDriverWithPhoto(driver, photo);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriver);
    }

    // -------------------------------------------------------------------------
    // Update driver photo separately
    // -------------------------------------------------------------------------
    @PutMapping(value = "/{driverId}/photo", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateDriverPhoto(
            @PathVariable Long driverId,
            @RequestPart("photo") MultipartFile photo) {

        log.info("API: Update photo for driverId {}", driverId);

        try {
            Driver updatedDriver = driverService.updateDriverPhoto(driverId, photo);
            return ResponseEntity.ok(updatedDriver);
        } catch (ResourceNotFoundException ex) {
            log.error("Driver not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            log.error("Invalid request: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error updating driver photo: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Something went wrong"));
        }
    }
    
    
    
    @PostMapping("/scanQr")
    public ResponseEntity<ApiResponse> scanQr(@RequestParam("qrData") String qrData) {
        log.info("Received QR scan request");

        try {
            ScanResultDTO result = driverService.driverScanResult(qrData);

            return ResponseEntity.ok(
                    ApiResponse.success("QR scan successful", result)
            );

        } catch (IllegalArgumentException e) {
            log.warn("Invalid QR input: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("QR scan failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to process QR code"));
        }
    }
    
    
}
