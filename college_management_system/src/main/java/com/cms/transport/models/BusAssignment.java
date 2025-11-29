package com.cms.transport.models;

import com.cms.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bus_id", "assignment_date"}),
                @UniqueConstraint(columnNames = {"driver_id", "assignment_date"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    // -----------------------------------------
    // Relations
    // -----------------------------------------
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
    @JoinColumn(name = "gps_id", nullable = true)
    private GPSDevice gpsDevice; // optional GPS per assignment

    // -----------------------------------------
    // Assignment Metadata
    // -----------------------------------------
    @Column(nullable = false)
    private LocalDate assignmentDate;  // date of assignment

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(length = 255)
    private String notes;              // optional admin notes

    private LocalDate endDate;         // optional, for multi-day assignments

    @CreationTimestamp
    private LocalDateTime assignedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
