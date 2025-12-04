package com.cms.transport.driverLeave.model;


import com.cms.common.enums.Status;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driverLeave.enums.ApprovalStatus;
import com.cms.transport.driverLeave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_leave",
        indexes = {
                @Index(name = "idx_driver_leave_driver_dates", columnList = "driver_id, start_date, end_date")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DriverLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(length = 255)
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

