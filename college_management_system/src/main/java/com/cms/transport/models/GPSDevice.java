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
@Table(name = "gps_devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpsId;

    // Unique Serial / IMEI / Device ID
    @Column(nullable = false, unique = true)
    private String deviceSerialNumber;

    private String provider; // Teltonika, SecuTrak, Letstrack, etc.

    @Enumerated(EnumType.STRING)
    private GPSStatus status = GPSStatus.ACTIVE; // ACTIVE, INACTIVE, DAMAGED, LOST

    // ---------------------------
    // Installed on BUS currently
    // Real world: GPS device can be moved from Bus A → Bus B
    // ---------------------------
    @OneToOne(mappedBy = "gpsDevice", fetch = FetchType.LAZY)
    private Bus bus;

    // ---------------------------
    // Assignment history
    // ---------------------------
    @OneToMany(mappedBy = "gpsDevice", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BusAssignment> busAssignments = new ArrayList<>();

    // ---------------------------
    // Health Status / Metadata
    // ---------------------------
    private LocalDateTime lastPingAt; // last time device sent location
//    private Integer batteryLevel;      // % if device has battery
//    private Boolean ignitionOn;        // last known ignition state

    // Installation metadata
//    private LocalDateTime installedAt;
//    private String installedBy;

    // ---------------------------
    // Audit
    // ---------------------------
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Utility
    public void addBusAssignment(BusAssignment assignment) {
        busAssignments.add(assignment);
        assignment.setGpsDevice(this);
    }
}
