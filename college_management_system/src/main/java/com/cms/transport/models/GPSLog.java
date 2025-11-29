package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gps_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------------
    // Relationship with GPS Device
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gps_id", nullable = false)
    private GPSDevice gpsDevice;

    // ---------------------------
    // Optional assignment reference
    // Helps identify BUS + DRIVER at that time
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private BusAssignment assignment;

    // ---------------------------
    // Core GPS Data
    // ---------------------------
    private Double latitude;
    private Double longitude;
//    private Double speed;
//    private String direction;      // N/E/S/W or degrees
//    private Double accuracy;       // GPS accuracy (meters)
//    private Double altitude;       // optional

    // ---------------------------
    // Device / Telemetry Data
    // ---------------------------
//    private Boolean ignitionOn;
//    private Integer batteryLevel;  // %
//    private String networkType;    // 2G/4G/Offline

    // ---------------------------
    // Event Type (for alerts)
    // ---------------------------
//    @Enumerated(EnumType.STRING)
//    private GPSEventType eventType;

    // Raw payload from GPS provider for debug/audit
//    @Column(columnDefinition = "TEXT")
//    private String rawPayload;

    // ---------------------------
    // Timestamps
    // ---------------------------
    private LocalDateTime recordedAt;       // time sent by device
    private LocalDateTime serverReceivedAt; // time backend saved
}
