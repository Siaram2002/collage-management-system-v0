package com.cms.transport.models;

import com.cms.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "bus_assignments",
    indexes = {
        @Index(
            name = "idx_bus_active_unique",
            columnList = "bus_id, assignment_date, active_flag",
            unique = true
        ),
        @Index(
            name = "idx_driver_active_unique",
            columnList = "driver_id, assignment_date, active_flag",
            unique = true
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gps_id")
    private GPSDevice gpsDevice;

    @Column(nullable = false)
    private LocalDate assignmentDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private LocalDate endDate;

    @Column(length = 255)
    private String notes;

    // ----------------------------
    // GENERATED COLUMN (IMPORTANT)
    // ----------------------------
    @Column(name = "active_flag", insertable = false, updatable = false)
    private Integer activeFlag;

    @CreationTimestamp
    private LocalDateTime assignedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
