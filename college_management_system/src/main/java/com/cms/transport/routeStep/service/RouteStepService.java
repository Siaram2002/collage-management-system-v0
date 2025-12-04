package com.cms.transport.routeStep.service;

import com.cms.transport.routeStep.dto.RouteStepDTO;

import java.util.List;

public interface RouteStepService {
    RouteStepDTO addStep(Long routeId, RouteStepDTO stepDTO);
    RouteStepDTO updateStep(Long stepId, RouteStepDTO stepDTO);
    RouteStepDTO getStepById(Long stepId);
    List<RouteStepDTO> getStepsByRoute(Long routeId);
    String deleteStep(Long stepId);
}
