package com.cms.notifications.module;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_read", columnList = "isRead"),
    @Index(name = "idx_notification_type", columnList = "type"),
    @Index(name = "idx_notification_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 50)
    private String type; // INFO, SUCCESS, WARNING, ERROR

    @Column(nullable = false, length = 50)
    private String category; // ATTENDANCE, STUDENT, BUS_PASS, SCAN, GENERAL

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    // Optional: Link to related entity (studentId, lectureId, etc.)
    @Column
    private Long referenceId;

    @Column(length = 50)
    private String referenceType; // STUDENT, LECTURE, BUS_PASS, etc.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Optional: Additional metadata as JSON string
    @Column(columnDefinition = "TEXT")
    private String metadata;
}

