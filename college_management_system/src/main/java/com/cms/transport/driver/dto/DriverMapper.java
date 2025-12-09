package com.cms.transport.driver.dto;





import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.enums.DriverStatus;
import com.cms.college.models.Contact;
import com.cms.college.models.Address;

public class DriverMapper {

    public static Driver toDriverEntity(DriverRegisterDTO dto) {

        // --- Map Address ---
        Address address = null;
        if (dto.getContact() != null && dto.getContact().getAddress() != null) {
            DriverRegisterDTO.AddressDTO ad = dto.getContact().getAddress();

            address = Address.builder()
                    .line1(ad.getLine1())
                    .line2(ad.getLine2())
                    .city(ad.getCity())
                    .district(ad.getDistrict())
                    .state(ad.getState())
                    .country(ad.getCountry())
                    .pin(ad.getPin())
                    .build();
        }

        // --- Map Contact ---
        Contact contact = null;
        if (dto.getContact() != null) {
            DriverRegisterDTO.ContactDTO ct = dto.getContact();

            contact = Contact.builder()
                    .phone(ct.getPhone())
                    .email(ct.getEmail())
                    .altPhone(ct.getAltPhone())
                    .address(address)
                    .build();
        }

        // --- Map Driver ---
        return Driver.builder()
                .fullName(dto.getFullName())
                .licenseNumber(dto.getLicenseNumber())
                .licenseExpiryDate(dto.getLicenseExpiryDate())
                .status(DriverStatus.INACTIVE)
                .contact(contact)
                .build();
    }


}
