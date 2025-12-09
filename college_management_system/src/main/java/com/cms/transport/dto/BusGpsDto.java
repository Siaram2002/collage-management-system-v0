package com.cms.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class BusGpsDto {
	
	private long busId;
	
	private String busNumber;
	
	
	private long driverId;
	
	private String driverName;
	
	private String driverPhone;
	
	private String driverEmail;
	

}
