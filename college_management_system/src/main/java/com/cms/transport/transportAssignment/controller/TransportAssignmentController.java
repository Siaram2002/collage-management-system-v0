package com.cms.transport.transportAssignment.controller;

import com.cms.transport.transportAssignment.dto.TransportAssignmentRequest;
import com.cms.transport.transportAssignment.dto.TransportAssignmentResponse;
import com.cms.transport.transportAssignment.service.TransportAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for TransportAssignment
 * Handles all CRUD operations.
 * Returns standardized success/failure JSON for frontend.
 */
@RestController
@RequestMapping("/api/transport-assignments")
@RequiredArgsConstructor
public class TransportAssignmentController {

    private final TransportAssignmentService assignmentService;

    // ----------------------------------------------------
    // CREATE ASSIGNMENT
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAssignment(@RequestBody TransportAssignmentRequest request) {
        TransportAssignmentResponse response = assignmentService.createAssignment(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ----------------------------------------------------
    // UPDATE ASSIGNMENT
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAssignment(
            @PathVariable Long id,
            @RequestBody TransportAssignmentRequest request) {
        TransportAssignmentResponse response = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ----------------------------------------------------
    // GET ALL ASSIGNMENTS
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAssignments() {
        List<TransportAssignmentResponse> list = assignmentService.getAllAssignments();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list
        ));
    }

    // ----------------------------------------------------
    // GET ASSIGNMENT BY ID
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAssignmentById(@PathVariable Long id) {
        TransportAssignmentResponse response = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ----------------------------------------------------
    // DELETE ASSIGNMENT (Soft Delete)
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Assignment deleted successfully"
        ));
    }
}
