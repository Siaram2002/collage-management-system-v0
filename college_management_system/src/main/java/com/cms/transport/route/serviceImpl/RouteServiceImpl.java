package com.cms.transport.route.serviceImpl;

import com.cms.transport.route.dto.RouteRequest;
import com.cms.transport.route.dto.RouteResponse;
import com.cms.transport.route.model.Route;
import com.cms.transport.route.repository.RouteRepository;
import com.cms.transport.route.service.RouteService;
import com.cms.transport.routeStep.dto.RouteStepDTO;
import com.cms.transport.routeStep.repository.RouteStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepo;
    private final RouteStepRepository stepRepo;

    @Override
    public RouteResponse createRoute(RouteRequest request) {
        if(routeRepo.existsByRouteNameIgnoreCase(request.getRouteName()))
            throw new RuntimeException("Route name already exists.");

        Route route = Route.builder()
                .routeName(request.getRouteName())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        return convertToResponse(routeRepo.save(route));
    }

    @Override
    public RouteResponse updateRoute(Long routeId, RouteRequest request) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setRouteName(request.getRouteName());
        route.setDescription(request.getDescription());
        route.setStatus(request.getStatus());

        return convertToResponse(routeRepo.save(route));
    }

    @Override
    public RouteResponse getRoute(Long routeId) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        return convertToResponse(route);
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteRoute(Long routeId) {
        if(!routeRepo.existsById(routeId))
            throw new RuntimeException("Route not found");

        stepRepo.deleteByRouteRouteId(routeId);
        routeRepo.deleteById(routeId);
        return "Route deleted successfully.";
    }

    private RouteResponse convertToResponse(Route route) {
        List<RouteStepDTO> steps = stepRepo.findByRouteRouteIdOrderByStepOrderAsc(route.getRouteId())
                .stream()
                .map(s -> RouteStepDTO.builder()
                        .stepId(s.getStepId())
                        .stopName(s.getStopName())
                        .stepOrder(s.getStepOrder())
                        .latitude(s.getLatitude())
                        .longitude(s.getLongitude())
                        .expectedArrivalTime(s.getExpectedArrivalTime())
                        .build())
                .collect(Collectors.toList());

        return RouteResponse.builder()
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())
                .description(route.getDescription())
                .status(route.getStatus())
                .steps(steps)
                .build();
    }
}
