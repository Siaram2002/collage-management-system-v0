package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "gps_logs",
       indexes = {
           @Index(name = "idx_gps_device_time", columnList = "gps_id, recordedAt"),
           @Index(name = "idx_assignment_recorded", columnList = "assignment_id, recordedAt")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GPSLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // device that reported the ping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gps_id", nullable = false)
    private GPSDevice gpsDevice;

    // optional assignment reference (so we can say which driver/bus at that time)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private BusAssignment assignment;

    // optional trip reference (morning/evening)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private TripSchedule tripSchedule;

    private Double latitude;
    private Double longitude;

    private Double speed;           // km/h (nullable)
    private Double heading;         // degrees (nullable)
    private Double accuracyMeters;  // nullable

    // telemetry (optional)
    private Boolean ignitionOn;
    private Integer batteryLevel;

    // device vs server times
    private LocalDateTime recordedAt;       // time sent by device (may be device clock)
    @CreationTimestamp
    private LocalDateTime serverReceivedAt; // saved time on server
}
