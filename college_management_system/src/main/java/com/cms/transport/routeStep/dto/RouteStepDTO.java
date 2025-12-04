package com.cms.transport.routeStep.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteStepDTO {
    private Long stepId;
    private String stopName;
    private Integer stepOrder;
    private Double latitude;
    private Double longitude;
    private String expectedArrivalTime;
}
