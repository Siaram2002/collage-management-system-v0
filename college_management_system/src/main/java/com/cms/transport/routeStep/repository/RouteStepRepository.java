package com.cms.transport.routeStep.repository;

import com.cms.transport.route.model.Route;

import com.cms.transport.routeStep.model.RouteStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStepRepository extends JpaRepository<RouteStep, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Get all steps belonging to a specific route.
     */
    List<RouteStep> findByRoute(Route route);

    /**
     * Find a route step by its stop name within a route.
     */
    List<RouteStep> findByRouteAndStopNameContainingIgnoreCase(Route route, String stopName);


    // -----------------------------------------------------
    // 📊 SORTING HELPERS
    // -----------------------------------------------------

    /**
     * Get all steps for a route sorted by step order ascending.
     */
    List<RouteStep> findByRouteOrderByStepOrderAsc(Route route);

    /**
     * Get all steps for a route sorted by step order descending.
     */
    List<RouteStep> findByRouteOrderByStepOrderDesc(Route route);

    List<RouteStep> findByRouteRouteIdOrderByStepOrderAsc(Long routeId);

    void deleteByRouteRouteId(Long routeId);



}
