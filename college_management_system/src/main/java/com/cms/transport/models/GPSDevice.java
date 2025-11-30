package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.transport.enums.GPSStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gps_devices",
       indexes = {
           @Index(name = "idx_device_serial", columnList = "deviceSerialNumber"),
           @Index(name = "idx_last_ping", columnList = "lastPingAt")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GPSDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpsId;

    @Column(nullable = false, unique = true)
    private String deviceSerialNumber;

    private String provider;

    @Enumerated(EnumType.STRING)
    private GPSStatus status = GPSStatus.ACTIVE;

    // Current bus installation (read-only backref)
    @OneToOne(mappedBy = "gpsDevice", fetch = FetchType.LAZY)
    private Bus bus;

    // Assignment history (many assignments can use same device over time)
    @OneToMany(mappedBy = "gpsDevice", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BusAssignment> busAssignments = new ArrayList<>();

    // Health telemetry
    private LocalDateTime lastPingAt;
    private Integer batteryLevel;   // nullable % (if device supports)
    private Boolean ignitionOn;     // last known ignition state

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addBusAssignment(BusAssignment assignment) {
        this.busAssignments.add(assignment);
        assignment.setGpsDevice(this);
    }
}
