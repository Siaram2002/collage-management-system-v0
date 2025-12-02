package com.cms.transport.driver.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.cms.transport.driver.enums.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_availability",
       indexes = {
           @Index(name = "idx_driver_avail_driver_date", columnList = "driver_id, startDate, endDate")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DriverAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(length = 512)
    private String reason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
