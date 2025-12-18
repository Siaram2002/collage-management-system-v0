package com.cms.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDTO {
    private String title;
    private String message;
    private String type; // INFO, SUCCESS, WARNING, ERROR
    private String category; // ATTENDANCE, STUDENT, BUS_PASS, SCAN, GENERAL
    private Long referenceId;
    private String referenceType;
    private String metadata;
}

