package com.cms.transport.route.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.cms.common.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "route_steps")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String stopName; // Name of the stop

    @Column(unique = true, nullable = false)
    private String stepCode; // Unique code for stop, e.g., "R101-S01"

    private Integer stepOrder; // Sequence in the route
    private String description; // Optional info about stop

    // ---------------- GPS COORDINATES ----------------
    private Double latitude;  // Optional: GPS latitude
    private Double longitude; // Optional: GPS longitude

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
