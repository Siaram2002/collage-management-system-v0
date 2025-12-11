package com.cms.transport.driver.controller;

import com.cms.busPass.BusPass;

import com.cms.common.ApiResponse;

import com.cms.common.exceptions.ResourceNotFoundException;
import com.cms.transport.driver.dto.DriverMapper;
import com.cms.transport.driver.dto.DriverRegisterDTO;
import com.cms.transport.driver.dto.ScanResultDTO;
import com.cms.transport.driver.model.QRScanLog;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.service.DriverService;
import com.cms.transport.driver.service.QRScanLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final QRScanLogService qrScanLogService;

    // -------------------------------------------------------------------------
    // Register a new driver (with optional photo)
    // -------------------------------------------------------------------------
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<Driver> createDriver(
            @RequestPart("driver") DriverRegisterDTO dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
    	
    	Driver driver=DriverMapper.toDriverEntity(dto);

        Driver createdDriver = driverService.createDriver(driver,photo);
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

    // -------------------------------------------------------------------------
    // Scan QR code
    // -------------------------------------------------------------------------
    @GetMapping("/scanQr")
    public ResponseEntity<ApiResponse> scanQr(@RequestParam("driverId") Long driverId,
                                              @RequestParam("qrData") String qrData) {

        log.info("Received QR scan request from driverId {} with qrData='{}'", driverId, qrData);

        try {
            // trim to remove any leading/trailing spaces
            if (qrData == null || qrData.trim().isEmpty()) {
                throw new IllegalArgumentException("QR data cannot be null or empty");
            }

            qrData = qrData.trim();

            BusPass result = driverService.driverScanResult(qrData, driverId);

            return ResponseEntity.ok(ApiResponse.success("QR scan successful", result));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid QR input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("QR scan failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ApiResponse.fail("Failed to process QR code"));
        }
    }


    // -------------------------------------------------------------------------
    // Get all QR scan logs by driver
    // -------------------------------------------------------------------------
    @GetMapping("/{driverId}/qrScanLogs")
    public ResponseEntity<ApiResponse> getQrScanLogsByDriver(@PathVariable Long driverId) {
        log.info("Fetching QR scan logs for driverId {}", driverId);

        try {
            List<QRScanLog> logs = qrScanLogService.getLogsByDriver(driverId);
            return ResponseEntity.ok(ApiResponse.success("QR scan logs fetched", logs));
        } catch (ResourceNotFoundException e) {
            log.warn("Driver not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching QR scan logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to fetch QR scan logs"));
        }
    }
    
    @PatchMapping("/{driverId}/status")
    public ResponseEntity<Driver> setDriverStatus(
            @PathVariable Long driverId,
            @RequestParam String status) {

        // status should be ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED
        Driver updatedDriver = driverService.updateStatus(driverId, status.toUpperCase());
        return ResponseEntity.ok(updatedDriver);
    }
}
