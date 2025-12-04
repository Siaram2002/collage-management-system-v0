package com.cms.transport.routeStep.controller;

import com.cms.transport.routeStep.dto.RouteStepDTO;
import com.cms.transport.routeStep.service.RouteStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RouteStepController {

    private final RouteStepService stepService;

    // ✅ Add Step to a Route
    @PostMapping("/routes/{routeId}/steps")
    public ResponseEntity<RouteStepDTO> addStep(@PathVariable Long routeId,
                                                @RequestBody RouteStepDTO stepDTO) {
        return ResponseEntity.ok(stepService.addStep(routeId, stepDTO));
    }

    // ✅ Update Step
    @PutMapping("/steps/{stepId}")
    public ResponseEntity<RouteStepDTO> updateStep(@PathVariable Long stepId,
                                                   @RequestBody RouteStepDTO stepDTO) {
        return ResponseEntity.ok(stepService.updateStep(stepId, stepDTO));
    }

    // ✅ Get Step by ID
    @GetMapping("/steps/{stepId}")
    public ResponseEntity<RouteStepDTO> getStepById(@PathVariable Long stepId) {
        return ResponseEntity.ok(stepService.getStepById(stepId));
    }

    // ✅ Get all Steps for a Route
    @GetMapping("/routes/{routeId}/steps")
    public ResponseEntity<List<RouteStepDTO>> getStepsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(stepService.getStepsByRoute(routeId));
    }

    // ✅ Delete Step
    @DeleteMapping("/steps/{stepId}")
    public ResponseEntity<String> deleteStep(@PathVariable Long stepId) {
        return ResponseEntity.ok(stepService.deleteStep(stepId));
    }
}
