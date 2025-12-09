package com.cms.transport.driver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRegisterDTO {

    private String fullName;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;

    // Embedded Contact DTO
    private ContactDTO contact;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContactDTO {
        private String phone;
        private String email;
        private String altPhone;

        private AddressDTO address;
    }

    // Embedded Address DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDTO {
        private String line1;
        private String line2;

        private String city;
        private String district;
        private String state;
        private String country;

        private String pin;
    }
}
