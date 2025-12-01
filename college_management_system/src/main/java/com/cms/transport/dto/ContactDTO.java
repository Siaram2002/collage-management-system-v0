package com.cms.transport.dto;

import lombok.*;
// Data Transfer Object for Driver's Contact Info
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactDTO {
    private String phone;
    private String email;
    private String altPhone;
}

