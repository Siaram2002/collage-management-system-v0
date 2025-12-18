package com.cms.attendance.lectureQrCode.repositories;


import com.cms.attendance.lectureQrCode.module.LectureQR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureQrRepository extends JpaRepository<LectureQR, Long> {

    Optional<LectureQR> findByQrPayload(String qrPayload);

    Optional<LectureQR> findByLecture_LectureId(Long lectureId);
}


//Find QR by UID (qrPayload) → used when student scans QR
//
//Find existing QR by lecture → used to expire old QR