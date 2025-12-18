package com.cms.attendance.lectureQrCode.service;


//import com.cms.attendance.lecture.module.Lecture;
//import com.cms.attendance.lecture.repositories.LectureRepository;
//import com.cms.attendance.lectureQrCode.module.LectureQR;
//import com.cms.attendance.lectureQrCode.repositories.LectureQrRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.UUID;

import com.cms.attendance.lecture.module.Lecture;
import com.cms.attendance.lecture.repositories.LectureRepository;
import com.cms.attendance.lectureQrCode.module.LectureQR;
import com.cms.attendance.lectureQrCode.repositories.LectureQrRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.UUID;          // ✅ Add this
import java.time.LocalDateTime; // ✅ Add this

@Service
@RequiredArgsConstructor
public class LectureQrService {

    private final LectureQrRepository lectureQrRepository;
    private final LectureRepository lectureRepository;
    private final LectureQrStorageService qrStorage;

    @Transactional
    public LectureQR generateQr(Long lectureId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // Expire old QR if exists
        lectureQrRepository.findByLecture_LectureId(lectureId)
                .ifPresent(old -> {
                    old.setStatus("EXPIRED");
                    lectureQrRepository.save(old);
                });

        // Create new QR
        LectureQR qr = new LectureQR();
        qr.setLecture(lecture);
        qr.setQrPayload(UUID.randomUUID().toString());
        qr.setExpiryTime(LocalDateTime.now().plusMinutes(60));
        qr.setStatus("ACTIVE");

        lectureQrRepository.save(qr);

        // Save QR image
        qrStorage.saveQr(qr.getQrPayload());

        return qr;
    }
}

