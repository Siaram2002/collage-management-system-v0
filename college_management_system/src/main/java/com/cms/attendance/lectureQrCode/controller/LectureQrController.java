package com.cms.attendance.lectureQrCode.controller;

import com.cms.attendance.lectureQrCode.module.LectureQR;
import com.cms.attendance.lectureQrCode.service.LectureQrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/lecture-qr")
@RequiredArgsConstructor
public class LectureQrController {


    private final LectureQrService lectureQrService;

    @PostMapping("/generate/{lectureId}")
    public ResponseEntity<?> generateQr(@PathVariable Long lectureId) {
        LectureQR qr = lectureQrService.generateQr(lectureId);
        return ResponseEntity.ok(qr.getQrPayload());
    }
}
