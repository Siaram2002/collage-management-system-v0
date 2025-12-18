package com.cms.notifications.repositories;

import com.cms.notifications.module.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByIsReadOrderByCreatedAtDesc(Boolean isRead, Pageable pageable);

    List<Notification> findByCategoryOrderByCreatedAtDesc(String category);

    List<Notification> findByTypeOrderByCreatedAtDesc(String type);

    Long countByIsRead(Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false")
    void markAllAsRead();

    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(LocalDateTime since);

    @Query("SELECT n FROM Notification n WHERE n.category = :category AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByCategory(String category, LocalDateTime since);
}

