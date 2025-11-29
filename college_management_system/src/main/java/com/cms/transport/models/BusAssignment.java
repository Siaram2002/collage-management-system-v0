package com.cms.transport.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bus_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bus_id", "assignment_date", "shift"}),
                @UniqueConstraint(columnNames = {"driver_id", "assignment_date", "shift"})
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

    // -----------------------------------------
    // Assignment Details
    // -----------------------------------------

    @Column(nullable = false)
    private LocalDate assignmentDate;    // day of assignment




    @Column(length = 255)
    private String notes;                // Optional admin note

    // For knowing when assignment ends
    private LocalDate endDate;

    // -----------------------------------------
    // Auditing
    // -----------------------------------------

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime assignedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
