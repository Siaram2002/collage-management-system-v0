package com.cms.transport.bus.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.cms.transport.bus.enums.MaintenanceType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_maintenance",
       indexes = {
           @Index(name = "idx_bus_maintenance_bus_date", columnList = "bus_id, startDate")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    private LocalDate startDate;
    private LocalDate expectedEndDate;

    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    @Column(length = 512)
    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
