package com.cms.transport.GPSDevice.dto;

import lombok.Data;

@Data
public class GPSPingUpdateRequest {
    private Integer batteryLevel;
    private Boolean ignitionOn;
}
