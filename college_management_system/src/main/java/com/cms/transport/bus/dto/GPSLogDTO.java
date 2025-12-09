package com.cms.transport.bus.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GPSLogDTO {
    private Long gpsId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private Double accuracyMeters;
    private Boolean ignitionOn;
    private Integer batteryLevel;
    private LocalDateTime recordedAt;
}
