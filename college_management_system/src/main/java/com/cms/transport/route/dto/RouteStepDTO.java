package com.cms.transport.route.dto;

import lombok.Data;
import com.cms.common.enums.Status;

@Data
public class RouteStepDTO {
    private Long stepId;
    private String stepCode; // Unique code for the stop
    private String stopName;
    private Integer stepOrder; // Sequence in the route
    private Double latitude; // Optional GPS info
    private Double longitude; // Optional GPS info
    private Status status = Status.ACTIVE; // Stop status
}
