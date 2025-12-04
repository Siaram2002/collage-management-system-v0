package com.cms.transport.routeStep.serviceImpl;

import com.cms.transport.route.model.Route;
import com.cms.transport.route.repository.RouteRepository;
import com.cms.transport.routeStep.dto.RouteStepDTO;
import com.cms.transport.routeStep.model.RouteStep;
import com.cms.transport.routeStep.repository.RouteStepRepository;
import com.cms.transport.routeStep.service.RouteStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteStepServiceImpl implements RouteStepService {

    private final RouteStepRepository stepRepo;
    private final RouteRepository routeRepo;

    @Override
    public RouteStepDTO addStep(Long routeId, RouteStepDTO stepDTO) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        RouteStep step = RouteStep.builder()
                .route(route)
                .stopName(stepDTO.getStopName())
                .stepOrder(stepDTO.getStepOrder())
                .latitude(stepDTO.getLatitude())
                .longitude(stepDTO.getLongitude())
                .expectedArrivalTime(stepDTO.getExpectedArrivalTime())
                .build();

        return convertToDTO(stepRepo.save(step));
    }

    @Override
    public RouteStepDTO updateStep(Long stepId, RouteStepDTO stepDTO) {
        RouteStep step = stepRepo.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found"));

        step.setStopName(stepDTO.getStopName());
        step.setStepOrder(stepDTO.getStepOrder());
        step.setLatitude(stepDTO.getLatitude());
        step.setLongitude(stepDTO.getLongitude());
        step.setExpectedArrivalTime(stepDTO.getExpectedArrivalTime());

        return convertToDTO(stepRepo.save(step));
    }

    @Override
    public RouteStepDTO getStepById(Long stepId) {
        return stepRepo.findById(stepId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Step not found"));
    }

    @Override
    public List<RouteStepDTO> getStepsByRoute(Long routeId) {
        return stepRepo.findByRouteRouteIdOrderByStepOrderAsc(routeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteStep(Long stepId) {
        if(!stepRepo.existsById(stepId)) throw new RuntimeException("Step not found");
        stepRepo.deleteById(stepId);
        return "Step deleted successfully.";
    }

    private RouteStepDTO convertToDTO(RouteStep step) {
        return RouteStepDTO.builder()
                .stepId(step.getStepId())
                .stopName(step.getStopName())
                .stepOrder(step.getStepOrder())
                .latitude(step.getLatitude())
                .longitude(step.getLongitude())
                .expectedArrivalTime(step.getExpectedArrivalTime())
                .build();
    }
}
