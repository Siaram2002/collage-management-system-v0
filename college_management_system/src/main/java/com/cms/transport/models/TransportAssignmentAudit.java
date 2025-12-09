package com.cms.transport.models;



import jakarta.persistence.*;
import lombok.*;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.enums.TransportStatus;
import com.cms.transport.route.models.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "transport_assignment_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportAssignmentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private Long assignmentId; // reference to original TransportAssignment

    // -----------------------------
    // Old values
    // -----------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_bus_id")
    private Bus oldBus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_driver_id")
    private Driver oldDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_route_id")
    private Route oldRoute;

    private LocalDate oldAssignmentDate;

    @Enumerated(EnumType.STRING)
    private TransportStatus oldStatus;

    // -----------------------------
    // New values
    // -----------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_bus_id")
    private Bus newBus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_driver_id")
    private Driver newDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_route_id")
    private Route newRoute;

    private LocalDate newAssignmentDate;

    @Enumerated(EnumType.STRING)
    private TransportStatus newStatus;

    @CreationTimestamp
    private LocalDateTime changedAt;   // timestamp of change
           // optional notes
}
