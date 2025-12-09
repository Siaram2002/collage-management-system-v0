package com.cms.transport.bus.controller;



import com.cms.common.ApiResponse;
import com.cms.transport.bus.dto.GPSLogDTO;
import com.cms.transport.bus.models.GPSLog;
import com.cms.transport.bus.service.GPSLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gps/logs")
@RequiredArgsConstructor
@Slf4j
public class GPSLogController {

    private final GPSLogService gpsLogService;

    @PostMapping
    public ResponseEntity<ApiResponse> addLog(@RequestBody GPSLogDTO dto) {
        GPSLog log = gpsLogService.addLog(dto);
        return ResponseEntity.ok(ApiResponse.success("Log saved successfully", log));
    }

    @GetMapping("/latest/{gpsId}")
    public ResponseEntity<ApiResponse> getLatest(@PathVariable Long gpsId) {
        GPSLog log = gpsLogService.getLatestLog(gpsId);
        return ResponseEntity.ok(ApiResponse.success("Latest location fetched", log));
    }

    @GetMapping("/history/{gpsId}")
    public ResponseEntity<ApiResponse> getHistory(
            @PathVariable Long gpsId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<GPSLog> logs = gpsLogService.getHistory(gpsId, start, end);
        return ResponseEntity.ok(ApiResponse.success("GPS history fetched", logs));
    }
}
