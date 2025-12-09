package com.cms.transport.route.controller;

import com.cms.common.ApiResponse;
import com.cms.transport.route.dto.RouteDTO;
import com.cms.transport.route.service.RouteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody RouteDTO dto) {
        log.info("API: Create route {}", dto.getRouteName());

        RouteDTO saved = routeService.createRoute(dto);

        return ResponseEntity.ok(
                ApiResponse.success("Route created successfully", saved)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody RouteDTO dto) {

        log.info("API: Update route {}", id);

        RouteDTO updated = routeService.updateRoute(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Route updated successfully", updated)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable Long id) {
        log.info("API: Get route {}", id);

        RouteDTO route = routeService.getRoute(id);

        return ResponseEntity.ok(
                ApiResponse.success("Route fetched successfully", route)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> all() {
        log.info("API: Get all routes");

        List<RouteDTO> routes = routeService.getAllRoutesWithoutSteps();

        return ResponseEntity.ok(
                ApiResponse.success("Routes fetched successfully", routes)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        log.info("API: Delete route {}", id);

        routeService.deleteRoute(id);

        return ResponseEntity.ok(
                ApiResponse.success("Route deleted successfully")
        );
    }
}
