package com.cms.notifications.service;

import com.cms.notifications.dto.NotificationCreateDTO;
import com.cms.notifications.dto.NotificationDTO;
import com.cms.notifications.module.Notification;
import com.cms.notifications.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationDTO createNotification(NotificationCreateDTO dto) {
        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .category(dto.getCategory())
                .referenceId(dto.getReferenceId())
                .referenceType(dto.getReferenceType())
                .metadata(dto.getMetadata())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Created notification: {} - {}", saved.getCategory(), saved.getTitle());
        return mapToDTO(saved);
    }

    @Transactional
    public void createNotification(String title, String message, String type, String category) {
        createNotification(NotificationCreateDTO.builder()
                .title(title)
                .message(message)
                .type(type)
                .category(category)
                .build());
    }

    @Transactional
    public void createNotification(String title, String message, String type, String category, Long referenceId, String referenceType) {
        createNotification(NotificationCreateDTO.builder()
                .title(title)
                .message(message)
                .type(type)
                .category(category)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build());
    }

    public Page<NotificationDTO> getNotifications(Boolean isRead, Pageable pageable) {
        Page<Notification> notifications;
        if (isRead != null) {
            notifications = notificationRepository.findByIsReadOrderByCreatedAtDesc(isRead, pageable);
        } else {
            notifications = notificationRepository.findAll(pageable);
        }
        return notifications.map(this::mapToDTO);
    }

    public List<NotificationDTO> getRecentNotifications(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return notificationRepository.findRecentNotifications(since)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByCategory(String category) {
        return notificationRepository.findByCategoryOrderByCreatedAtDesc(category)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount() {
        return notificationRepository.countByIsRead(false);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .category(notification.getCategory())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .createdAt(notification.getCreatedAt())
                .metadata(notification.getMetadata())
                .build();
    }
}

