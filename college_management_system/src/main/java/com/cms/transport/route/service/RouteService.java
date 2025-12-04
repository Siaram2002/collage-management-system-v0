package com.cms.transport.route.service;

import com.cms.transport.route.dto.RouteRequest;
import com.cms.transport.route.dto.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(RouteRequest request);
    RouteResponse updateRoute(Long routeId, RouteRequest request);
    RouteResponse getRoute(Long routeId);
    List<RouteResponse> getAllRoutes();
    String deleteRoute(Long routeId);
}
