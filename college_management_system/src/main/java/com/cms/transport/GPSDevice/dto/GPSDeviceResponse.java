package com.cms.transport.GPSDevice.dto;

import com.cms.transport.GPSDevice.enums.GPSStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GPSDeviceResponse {

    private Long gpsId;
    private String deviceSerialNumber;
    private String provider;
    private GPSStatus status;

    private Integer batteryLevel;
    private Boolean ignitionOn;

    private LocalDateTime lastPingAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
