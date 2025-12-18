package com.cms.transport.controller;

import com.cms.common.ApiResponse;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.dto.AssignmentResponseDTO;
import com.cms.transport.dto.BusGpsDto;
import com.cms.transport.dto.TransportAssignmentDTO;
import com.cms.transport.models.TransportAssignment;
import com.cms.transport.service.TransportAssignmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/transport/assignments")
@RequiredArgsConstructor
public class TransportAssignmentController {

    private final TransportAssignmentService assignmentService;

    // ---------------- CREATE ----------------
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody TransportAssignmentDTO dto) {
        log.info("API: Create transport assignment");

        TransportAssignment saved = assignmentService.createAssignment(dto);

        return ResponseEntity.ok(
                ApiResponse.success("Assignment created successfully", mapEntityToDto(saved))
        );
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody TransportAssignmentDTO dto) {
        log.info("API: Update transport assignment {}", id);

        TransportAssignment updated = assignmentService.updateAssignment(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Assignment updated successfully", mapEntityToDto(updated))
        );
    }

    // ---------------- GET BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable Long id) {
        log.info("API: Get transport assignment {}", id);

        TransportAssignment assignment = assignmentService.getAssignment(id);

        return ResponseEntity.ok(
                ApiResponse.success("Assignment retrieved successfully", mapEntityToDto(assignment))
        );
    }

    // ---------------- GET ALL -----------------
    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        log.info("API: Get all transport assignments");

        // Return full entities with driver, bus, and route details
        List<TransportAssignment> assignments = assignmentService.getAllAssignments();
        
        // Map to DTOs with full details
        List<TransportAssignmentDTO> list = assignments.stream()
                .map(this::mapEntityToDtoWithDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("All assignments fetched successfully", list)
        );
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        log.info("API: Delete transport assignment {}", id);

        assignmentService.deleteAssignment(id);

        return ResponseEntity.ok(
                ApiResponse.success("Assignment deleted successfully")
        );
    }

    // ---------------- MAPPING HELPERS ----------------
    private TransportAssignmentDTO mapEntityToDto(TransportAssignment entity) {
        TransportAssignmentDTO dto = new TransportAssignmentDTO();
        dto.setAssignmentId(entity.getAssignmentId());
        if (entity.getBus() != null) {
            dto.setBusId(entity.getBus().getBusId());
        }
        if (entity.getDriver() != null) {
            dto.setDriverId(entity.getDriver().getDriverId());
        }
        if (entity.getRoute() != null) {
            dto.setRouteId(entity.getRoute().getRouteId());
        }
        dto.setAssignmentDate(entity.getAssignmentDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }
    
    // Map entity to DTO with full driver, bus, and route details
    private TransportAssignmentDTO mapEntityToDtoWithDetails(TransportAssignment entity) {
        TransportAssignmentDTO dto = mapEntityToDto(entity);
        
        // Add nested objects for frontend
        if (entity.getDriver() != null) {
            dto.setDriver(entity.getDriver());
        }
        if (entity.getBus() != null) {
            dto.setBus(entity.getBus());
        }
        if (entity.getRoute() != null) {
            dto.setRoute(entity.getRoute());
        }
        
        return dto;
    }
    
    @GetMapping("/route/{routeId}/buses")
    public ResponseEntity<ApiResponse> getBusesByRoute(@PathVariable Long routeId) {

        List<BusGpsDto> buses = assignmentService.getBusesByRoute(routeId);

        return ResponseEntity.ok(
                ApiResponse.success("Buses for the route fetched successfully", buses)
        );
    }
    private final TransportAssignmentService transportAssignmentService;

    // ============================================================
    // GET ALL ASSIGNMENTS (TABLE VIEW)
    // ============================================================
    @GetMapping("/table")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignmentsForTable() {

        return ResponseEntity.ok(
                transportAssignmentService.getAllAssignmentsForTable()
        );
    }
}
