package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.transport.bus.models.Bus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.enums.TransportStatus;
import com.cms.transport.route.models.Route;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transport_assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TransportAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    // -----------------------------
    // ONE DRIVER ↔ ONE ASSIGNMENT
    // -----------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true, nullable = false)
    @JsonIgnoreProperties({"assignment"})   // avoid recursion
    private Driver driver;

    // -----------------------------
    // ONE BUS ↔ ONE ASSIGNMENT
    // -----------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", unique = true, nullable = false)
    @JsonBackReference(value = "bus-assignment")
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonBackReference(value = "route-assignment")
    private Route route;


    private LocalDate assignmentDate;

    @Enumerated(EnumType.STRING)
    private TransportStatus status = TransportStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
