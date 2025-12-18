package com.cms.transport.route.service.imp;

import com.cms.transport.route.dto.RouteDTO;
import com.cms.transport.route.dto.RouteStepDTO;
import com.cms.transport.route.models.Route;
import com.cms.transport.route.models.RouteStep;
import com.cms.transport.route.repository.RouteRepository;
import com.cms.transport.route.service.RouteService;
import com.cms.common.enums.Status;
import com.cms.common.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepo;

    @Override
    @Transactional
    public RouteDTO createRoute(RouteDTO dto) {
        log.info("Creating new route: {}", dto.getRouteName());

        Route route = new Route();
        route.setRouteName(dto.getRouteName());
        route.setDescription(dto.getDescription());
        route.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.ACTIVE);
        route.setRouteCode(dto.getRouteCode() != null ? dto.getRouteCode() : generateRouteCode());

        // Map steps if provided
        if (dto.getSteps() != null) {
            List<RouteStep> steps = dto.getSteps().stream()
                    .map(s -> RouteStep.builder()
                            .stopName(s.getStopName())
                            .stepOrder(s.getStepOrder())
                            .latitude(s.getLatitude())
                            .longitude(s.getLongitude())
                            .status(s.getStatus() != null ? s.getStatus() : Status.ACTIVE)
                            .stepCode(s.getStepCode() != null ? s.getStepCode() : generateStepCode())
                            .route(route)
                            .build())
                    .collect(Collectors.toList());
            route.setSteps(steps);
        }

        Route saved = routeRepo.save(route);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public RouteDTO updateRoute(Long routeId, RouteDTO dto) {
        log.info("Updating route {}", routeId);

        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        route.setRouteName(dto.getRouteName());
        route.setDescription(dto.getDescription());
        route.setStatus(dto.getStatus() != null ? dto.getStatus() : route.getStatus());

        if (dto.getRouteCode() != null) route.setRouteCode(dto.getRouteCode());

        // Clear existing steps and add new ones
        route.getSteps().clear();
        if (dto.getSteps() != null) {
            List<RouteStep> newSteps = dto.getSteps().stream()
                    .map(s -> RouteStep.builder()
                            .stopName(s.getStopName())
                            .stepOrder(s.getStepOrder())
                            .latitude(s.getLatitude())
                            .longitude(s.getLongitude())
                            .status(s.getStatus() != null ? s.getStatus() : Status.ACTIVE)
                            .stepCode(s.getStepCode() != null ? s.getStepCode() : generateStepCode())
                            .route(route)
                            .build())
                    .collect(Collectors.toList());
            route.getSteps().addAll(newSteps);
        }

        return convertToDTO(routeRepo.save(route));
    }

    @Override
    public RouteDTO getRoute(Long routeId) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        return convertToDTO(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> getAllRoutes() {
        // Fetch routes and initialize steps (lazy loading requires transaction)
        List<Route> routes = routeRepo.findAll();
        // Trigger lazy loading of steps by accessing them
        routes.forEach(route -> {
            if (route.getSteps() != null) {
                route.getSteps().size(); // Force initialization
            }
        });
        return routes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoute(Long routeId) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        routeRepo.delete(route);
    }

    // ------------------ DTO Converter ------------------
    private RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setRouteCode(route.getRouteCode());
        dto.setRouteName(route.getRouteName());
        dto.setDescription(route.getDescription());
        dto.setStatus(route.getStatus());

        if (route.getSteps() != null) {
            dto.setSteps(route.getSteps().stream().map(step -> {
                RouteStepDTO s = new RouteStepDTO();
                s.setStepId(step.getStepId());
                s.setStepCode(step.getStepCode());
                s.setStopName(step.getStopName());
                s.setStepOrder(step.getStepOrder());
                s.setLatitude(step.getLatitude());
                s.setLongitude(step.getLongitude());
                s.setStatus(step.getStatus());
                return s;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
    
    @Override
    public List<RouteDTO> getAllRoutesWithoutSteps() {
        return routeRepo.findAll().stream()
                .map(route -> {
                    RouteDTO dto = new RouteDTO();
                    dto.setRouteId(route.getRouteId());
                    dto.setRouteCode(route.getRouteCode());
                    dto.setRouteName(route.getRouteName());
                    dto.setDescription(route.getDescription());
                    dto.setStatus(route.getStatus());
                    // DO NOT include steps
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // ------------------ Helper Methods ------------------
    private String generateRouteCode() {
        return "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateStepCode() {
        return "S-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
