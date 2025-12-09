package com.cms.transport.route.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.cms.common.enums.Status;
import com.cms.transport.models.TransportAssignment;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(unique = true, nullable = false)
    private String routeCode; // Unique code for the route, e.g., "R101"

    @Column(nullable = false)
    private String routeName;

    private String description;
    
    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "route-assignment")
    private List<TransportAssignment> assignments;


    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<RouteStep> steps = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
