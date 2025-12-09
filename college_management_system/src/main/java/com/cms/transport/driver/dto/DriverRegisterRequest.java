package com.cms.transport.driver.dto;

import lombok.*;
import java.time.LocalDate;

import com.cms.transport.dto.AddressDTO;
import com.cms.transport.dto.ContactDTO;

// DTO for incoming Driver registration request
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRegisterRequest {
	private String role; // Must be DRIVER
	private String fullName;
	private String licenseNumber;
	private LocalDate licenseExpiryDate;
	private ContactDTO contact;
	private AddressDTO address;
	private String photoUrl;
}
