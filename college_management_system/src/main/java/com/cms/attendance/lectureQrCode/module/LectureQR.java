package com.cms.attendance.lectureQrCode.module;

import com.cms.attendance.lecture.module.Lecture;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "lecture_qr",
        indexes = {
                @Index(name = "idx_qr_expiry", columnList = "expiry_time")
        }
)
public class LectureQR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qrId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false, unique = true)
    private Lecture lecture;

    @Column(nullable = false, length = 500)
    private String qrPayload;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private String status; // ACTIVE / EXPIRED

    @CreationTimestamp
    private LocalDateTime createdAt;
}

