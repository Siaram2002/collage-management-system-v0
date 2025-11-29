package com.cms.transport.models;

import java.time.LocalDateTime;

import com.cms.common.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gps_devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GPSDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpsId;

    @Column(nullable = false)
    private String deviceSerialNumber;

    private String provider;

    private Double lastLatitude;
    private Double lastLongitude;

    private LocalDateTime lastUpdatedTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}
