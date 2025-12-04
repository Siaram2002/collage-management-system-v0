package com.cms.transport.bus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusRequest {

    private String busNumber;
    private String registrationNumber;
    private Integer seatingCapacity;
    private Long gpsId;   // optional
}
