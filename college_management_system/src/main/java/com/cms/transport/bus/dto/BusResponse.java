package com.cms.transport.bus.dto;

import com.cms.transport.bus.enums.BusStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusResponse {

    private Long busId;
    private String busNumber;
    private String registrationNumber;
    private Integer seatingCapacity;
    private BusStatus status;

    private Long gpsId;
}
