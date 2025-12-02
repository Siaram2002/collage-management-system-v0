package com.cms.transport.repositories;

import com.cms.common.enums.Status;
import com.cms.transport.route.models.Route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    /**
     * Find all routes having the given status (ACTIVE / INACTIVE).
     */
    List<Route> findByStatus(Status status);

    /**
     * Check if a route with the same name already exists.
     */
    boolean existsByRouteName(String routeName);

    /**
     * Search routes containing the given keyword in name or description.
     */
    List<Route> findByRouteNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword,
            String descriptionKeyword
    );
}
