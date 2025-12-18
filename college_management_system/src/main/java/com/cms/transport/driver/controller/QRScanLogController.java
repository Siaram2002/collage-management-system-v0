package com.cms.transport.driver.controller;

import com.cms.common.ApiResponse;
import com.cms.transport.driver.model.QRScanLog;
import com.cms.transport.driver.service.QRScanLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/qr-scan-logs")
@RequiredArgsConstructor
public class QRScanLogController {

    private final QRScanLogService qrScanLogService;

    /**
     * Get all QR scan logs (for admin/reports)
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLogs() {
        log.info("Fetching all QR scan logs");

        try {
            List<QRScanLog> logs = qrScanLogService.getAllLogs();
            return ResponseEntity.ok(ApiResponse.success("QR scan logs fetched successfully", logs));
        } catch (Exception e) {
            log.error("Error fetching QR scan logs", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Failed to fetch QR scan logs"));
        }
    }

    /**
     * Get QR scan logs by driver ID
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse> getLogsByDriver(@PathVariable Long driverId) {
        log.info("Fetching QR scan logs for driverId {}", driverId);

        try {
            List<QRScanLog> logs = qrScanLogService.getLogsByDriver(driverId);
            return ResponseEntity.ok(ApiResponse.success("QR scan logs fetched successfully", logs));
        } catch (Exception e) {
            log.error("Error fetching QR scan logs for driver {}", driverId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Failed to fetch QR scan logs"));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse> getAllLogReports() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "QR scan table data fetched",
                        qrScanLogService.getAllLogReports()
                )
        );
    }

}

