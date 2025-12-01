package com.cms.transport.controller;

import com.cms.transport.dto.DriverRegisterRequest;
import com.cms.transport.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // ================== Single Registration ==================
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerDriver(@RequestBody DriverRegisterRequest request) {
        Map<String, Object> response = driverService.registerDriver(request);
        HttpStatus status = "SUCCESS".equals(response.get("status")) ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(response, status);
    }

    // ================== Bulk Upload Excel =====================
    @PostMapping("/bulk-upload")
    public ResponseEntity<Map<String, Object>> uploadDriverExcel(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILED", "message", "File is required"));
        }

        Map<String, Object> response = driverService.bulkUploadDriversExcel(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}