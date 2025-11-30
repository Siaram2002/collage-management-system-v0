package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_steps", indexes = {
        @Index(name = "idx_route_step", columnList = "route_id, stepOrder")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(nullable = false)
    private String stopName;

    private Integer stepOrder;

    private Double latitude;
    private Double longitude;

    private String expectedArrivalTime; // optional human-friendly
}
