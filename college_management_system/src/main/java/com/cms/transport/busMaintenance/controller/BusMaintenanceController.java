package com.cms.transport.busMaintenance.controller;

import com.cms.transport.busMaintenance.dto.BusMaintenanceRequest;
import com.cms.transport.busMaintenance.dto.BusMaintenanceResponse;
import com.cms.transport.busMaintenance.service.BusMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class BusMaintenanceController {

    private final BusMaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<BusMaintenanceResponse> create(@RequestBody BusMaintenanceRequest request) {
        return ResponseEntity.ok(maintenanceService.createMaintenance(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusMaintenanceResponse> update(@PathVariable("id") Long id,
                                                         @RequestBody BusMaintenanceRequest request) {
        return ResponseEntity.ok(maintenanceService.updateMaintenance(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusMaintenanceResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(maintenanceService.getMaintenance(id));
    }

    @GetMapping
    public ResponseEntity<List<BusMaintenanceResponse>> getAll() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenance());
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<BusMaintenanceResponse>> getByBus(@PathVariable("busId") Long busId) {
        return ResponseEntity.ok(maintenanceService.getMaintenanceByBus(busId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        return ResponseEntity.ok(maintenanceService.deleteMaintenance(id));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam Long busId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        boolean available = maintenanceService.isBusAvailableForMaintenance(busId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("busId", busId);
        response.put("available", available);
        response.put("startDate", startDate);
        response.put("endDate", endDate);

        return ResponseEntity.ok(response);
    }

}
