package com.cms.transport.driver.model;


import com.cms.students.models.Student;
import com.cms.transport.bus.models.Bus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.route.models.Route;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_scan_logs",
       indexes = {
           @Index(name = "idx_qr_scan_student", columnList = "student_id"),
           @Index(name = "idx_qr_scan_bus", columnList = "bus_id"),
           @Index(name = "idx_qr_scan_driver", columnList = "driver_id"),
           @Index(name = "idx_qr_scan_route", columnList = "route_id"),
           @Index(name = "idx_qr_scan_time", columnList = "scanned_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRScanLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- STUDENT ----------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // ---------------- DRIVER ----------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // ---------------- BUS ----------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = true)
    private Bus bus;

    // ---------------- ROUTE ----------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = true)
    private Route route;

    // ---------------- TIMESTAMP ----------------
    @CreationTimestamp
    @Column(name = "scanned_at", updatable = false)
    private LocalDateTime scannedAt;

    // Optional notes or status
    @Column(length = 255)
    private String notes;

}
