package com.cms.college.dto;

import com.cms.common.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMasterDTO {

    // ---------- AdminMaster ----------
    private Long adminId;
    private String fullName;
    private String photoUrl;
    private Status adminStatus; // NEW: add admin status

    // ---------- Contact ----------
    private Long contactId;
    private String phone;
    private String email;
    private String altPhone;

    // ---------- Address ----------
    private Long addressId;
    private String line1;
    private String line2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pin;
    private Double latitude;
    private Double longitude;
}
