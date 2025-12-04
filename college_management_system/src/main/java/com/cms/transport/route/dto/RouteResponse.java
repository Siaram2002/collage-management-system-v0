package com.cms.transport.route.dto;

import com.cms.common.enums.Status;
import com.cms.transport.routeStep.dto.RouteStepDTO;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteResponse {
    private Long routeId;
    private String routeName;
    private String description;
    private Status status;
    private List<RouteStepDTO> steps;
}
