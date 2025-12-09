package com.cms.transport.route.service;



import com.cms.transport.route.dto.RouteDTO;
import java.util.List;

public interface RouteService {
    RouteDTO createRoute(RouteDTO dto);
    RouteDTO updateRoute(Long routeId, RouteDTO dto);
    RouteDTO getRoute(Long routeId);
    List<RouteDTO> getAllRoutes();
    void deleteRoute(Long routeId);
	List<RouteDTO> getAllRoutesWithoutSteps();
}
