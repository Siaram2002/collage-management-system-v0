package com.cms.attendance.lectureQrCode.service;



//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;



import com.google.zxing.BarcodeFormat;
//import com.google.zxing.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;   // ⭐ IMPORTANT
@Component
@Slf4j
public class LectureQrStorageService {

    private static final String BASE_DIR = "lecture/qrImages";
    private static final String URL_PREFIX = "/lecture/qrImages/";

    public LectureQrStorageService() throws Exception {
        Files.createDirectories(Paths.get(BASE_DIR));
    }

    public String saveQr(String qrPayload) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrPayload, BarcodeFormat.QR_CODE, 300, 300);
            Path path = Paths.get(BASE_DIR, qrPayload + ".png");
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);
            return URL_PREFIX + qrPayload + ".png";
        } catch (Exception e) {
            log.error("Failed to generate QR", e);
            throw new RuntimeException("Failed to generate lecture QR");
        }
    }
}
//✅ Now you can generate a physical QR image for each lecture.