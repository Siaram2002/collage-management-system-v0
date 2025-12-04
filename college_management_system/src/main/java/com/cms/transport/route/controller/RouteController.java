package com.cms.transport.route.controller;

import com.cms.transport.route.dto.RouteRequest;
import com.cms.transport.route.dto.RouteResponse;
import com.cms.transport.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    // ✅ Create a new Route
    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    // ✅ Update existing Route
    @PutMapping("/{routeId}")
    public ResponseEntity<RouteResponse> updateRoute(@PathVariable Long routeId,
                                                     @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(routeId, request));
    }

    // ✅ Get a Route by ID (with steps)
    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeService.getRoute(routeId));
    }

    // ✅ Get all Routes
    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // ✅ Delete a Route
    @DeleteMapping("/{routeId}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeService.deleteRoute(routeId));
    }
}
