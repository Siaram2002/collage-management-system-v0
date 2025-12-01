package com.cms.transport.dto;

import lombok.*;

// Data Transfer Object for Driver's Address Info
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String line1;
    private String line2;
    private String district;
    private String city;
    private String state;
    private String country;
    private String pin;
}
