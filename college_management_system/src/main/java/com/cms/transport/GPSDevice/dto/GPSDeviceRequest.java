package com.cms.transport.GPSDevice.dto;

import com.cms.transport.GPSDevice.enums.GPSStatus;
import lombok.Data;

@Data
public class GPSDeviceRequest {

    private String deviceSerialNumber;
    private String provider;
    private GPSStatus status = GPSStatus.AVAILABLE;

    private Integer batteryLevel;
    private Boolean ignitionOn;
}
