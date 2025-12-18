package com.cms.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String title;
    private String message;
    private String type;
    private String category;
    private Boolean isRead;
    private Long referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
    private String metadata;
}

