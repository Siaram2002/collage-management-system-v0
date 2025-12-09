package com.cms.transport.bus.models;

import com.cms.transport.enums.GPSStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gps_devices",
       indexes = {
           @Index(name = "idx_device_serial", columnList = "device_serial_number"),
           @Index(name = "idx_last_ping", columnList = "last_ping_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpsId;

    @Column(name = "device_serial_number", nullable = false, unique = true, length = 100)
    private String deviceSerialNumber;

    @Column(length = 100)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GPSStatus status = GPSStatus.ACTIVE;

    // backref to Bus (read-only)
    @OneToOne(mappedBy = "gpsDevice", fetch = FetchType.LAZY)
    @JsonBackReference
    private Bus bus;

    // Telemetry / health
    @Column(name = "last_ping_at")
    private LocalDateTime lastPingAt;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "ignition_on")
    private Boolean ignitionOn;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
