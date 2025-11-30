package com.cms.transport.models;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cms.college.models.User;
import com.cms.transport.enums.LeaveStatus;
import com.cms.transport.enums.LeaveType;
import com.cms.transport.enums.TripType;


@Entity
@Table(
    name = "driver_leave_request",
    indexes = {
        @Index(name = "idx_driver_leave_date", columnList = "driver_id, startDate, endDate")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TripType shift; // MORNING / EVENING

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // SICK / PERSONAL / OTHER

    @Column(length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING; // PENDING / APPROVED / REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User reviewedBy;

    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime reviewedAt;
}
