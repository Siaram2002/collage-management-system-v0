package com.cms.college.service.imp;

import com.cms.college.dto.AdminMasterDTO;
import com.cms.college.models.AdminMaster;
import com.cms.college.models.Address;
import com.cms.college.models.Contact;

public class AdminMasterMapper {

    // ---------------- Entity → DTO ----------------
    public static AdminMasterDTO toDTO(AdminMaster admin) {
        if (admin == null) return null;

        Contact contact = admin.getContact();
        Address address = contact != null ? contact.getAddress() : null;

        return AdminMasterDTO.builder()
                .adminId(admin.getId())
                .fullName(admin.getFullName())
                .photoUrl(admin.getPhotoUrl())
                .adminStatus(admin.getAdminStatus()) // map status
                .contactId(contact != null ? contact.getId() : null)
                .phone(contact != null ? contact.getPhone() : null)
                .email(contact != null ? contact.getEmail() : null)
                .altPhone(contact != null ? contact.getAltPhone() : null)
                .addressId(address != null ? address.getId() : null)
                .line1(address != null ? address.getLine1() : null)
                .line2(address != null ? address.getLine2() : null)
                .city(address != null ? address.getCity() : null)
                .district(address != null ? address.getDistrict() : null)
                .state(address != null ? address.getState() : null)
                .country(address != null ? address.getCountry() : null)
                .pin(address != null ? address.getPin() : null)
                .latitude(address != null ? address.getLatitude() : null)
                .longitude(address != null ? address.getLongitude() : null)
                .build();
    }

    // ---------------- DTO → Entity ----------------
    public static AdminMaster toEntity(AdminMasterDTO dto, Contact contact) {
        if (dto == null) return null;

        return AdminMaster.builder()
                .id(dto.getAdminId())
                .fullName(dto.getFullName())
                .photoUrl(dto.getPhotoUrl())
                .adminStatus(dto.getAdminStatus() != null ? dto.getAdminStatus() : com.cms.common.enums.Status.ACTIVE) // use DTO value or default
                .contact(contact)
                .build();
    }

    // ---------------- DTO → Contact Entity ----------------
    public static Contact toContactEntity(AdminMasterDTO dto, Address address) {
        if (dto == null) return null;

        return Contact.builder()
                .id(dto.getContactId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .altPhone(dto.getAltPhone())
                .address(address)
                .build();
    }

    // ---------------- DTO → Address Entity ----------------
    public static Address toAddressEntity(AdminMasterDTO dto) {
        if (dto == null) return null;

        return Address.builder()
                .id(dto.getAddressId())
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .state(dto.getState())
                .country(dto.getCountry())
                .pin(dto.getPin())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }
}
