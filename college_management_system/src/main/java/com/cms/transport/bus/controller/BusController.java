package com.cms.transport.bus.controller;


import com.cms.transport.bus.dto.BusRequest;
import com.cms.transport.bus.service.BusService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    public ResponseEntity<?> createBus(@RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.createBus(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBus(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBus(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBus(
            @PathVariable Long id,
            @RequestBody BusRequest request
    ) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok("Bus deleted (soft delete).");
    }
}

