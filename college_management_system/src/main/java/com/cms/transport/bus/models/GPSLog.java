package com.cms.transport.bus.models;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.cms.transport.models.TransportAssignment;


import java.time.LocalDateTime;

@Entity
@Table(name = "gps_logs",
       indexes = {
           @Index(name = "idx_gps_device_time", columnList = "gps_id, recorded_at"),
         
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // device that reported the ping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gps_id", nullable = false)
    private GPSDevice gpsDevice;

   
  

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "speed")
    private Double speed;           // km/h

    @Column(name = "heading")
    private Double heading;         // degrees

    @Column(name = "accuracy_meters")
    private Double accuracyMeters;

    @Column(name = "ignition_on")
    private Boolean ignitionOn;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    // time reported by device (may be device clock)
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    // server stored timestamp
    @CreationTimestamp
    @Column(name = "server_received_at", updatable = false)
    private LocalDateTime serverReceivedAt;


}
