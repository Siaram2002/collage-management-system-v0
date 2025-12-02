package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.cms.transport.bus.enums.TripStatus;
import com.cms.transport.bus.enums.TripType;

@Entity
@Table(name = "trip_schedules",
       indexes = {
           @Index(name = "idx_trip_assignment_type", columnList = "assignment_id, tripType"),
           @Index(name = "idx_trip_planned_start", columnList = "plannedStart")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private TransportAssignment busAssignment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripType tripType;  // MORNING / EVENING

    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;

    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;

    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus = TripStatus.PLANNED;

    @Column(length = 512)
    private String notes;
}
