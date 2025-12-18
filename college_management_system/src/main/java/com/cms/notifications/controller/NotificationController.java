package com.cms.notifications.controller;

import com.cms.common.ApiResponse;
import com.cms.notifications.dto.NotificationCreateDTO;
import com.cms.notifications.dto.NotificationDTO;
import com.cms.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationDTO> notifications = notificationService.getNotifications(isRead, pageable);
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", notifications));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse> getRecentNotifications(
            @RequestParam(defaultValue = "24") int hours) {
        List<NotificationDTO> notifications = notificationService.getRecentNotifications(hours);
        return ResponseEntity.ok(ApiResponse.success("Recent notifications fetched successfully", notifications));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getNotificationsByCategory(
            @PathVariable String category) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Category notifications fetched successfully", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount() {
        Long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.success("Unread count fetched successfully", count));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createNotification(
            @RequestBody NotificationCreateDTO dto) {
        NotificationDTO notification = notificationService.createNotification(dto);
        return ResponseEntity.ok(ApiResponse.success("Notification created successfully", notification));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }
}

