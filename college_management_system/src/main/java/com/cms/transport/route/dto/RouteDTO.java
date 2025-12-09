package com.cms.transport.route.dto;

import lombok.Data;
import java.util.List;
import com.cms.common.enums.Status;

@Data
public class RouteDTO {
    private Long routeId;
    private String routeCode; // Unique route code
    private String routeName;
    private String description;
    private Status status = Status.ACTIVE; // Default
    private List<RouteStepDTO> steps; // Stops in order
}
