package com.cms.students.services;

import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import com.cms.students.services.media.QRFileStorageService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentQRCodeService {

    private final StudentRepository studentRepository;
    private final QRFileStorageService qrFileStorageService;
    private final QRCodeWriter qrCodeWriter = new QRCodeWriter();

    /**
     * Generate and store QR code for a single student
     * QR code contains the student's roll number
     */
    @Transactional
    public String generateQRCodeForStudent(Student student) {
        if (student == null || student.getRollNumber() == null || student.getRollNumber().isEmpty()) {
            throw new IllegalArgumentException("Student and roll number are required");
        }

        try {
            // Generate QR code containing roll number
            String qrData = student.getRollNumber();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrBytes = outputStream.toByteArray();

            // Store QR code using roll number as unique identifier
            String qrUrl = qrFileStorageService.storeStudentQr(student.getRollNumber(), qrBytes);

            // Update student with QR code URL
            student.setQrCodeUrl(qrUrl);
            studentRepository.save(student);

            log.info("Generated and stored QR code for student: {} at {}", student.getRollNumber(), qrUrl);
            return qrUrl;

        } catch (Exception e) {
            log.error("Error generating QR code for student: {}", student.getRollNumber(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate QR codes for all students who don't have one yet
     */
    @Transactional
    public int generateQRCodesForAllStudents() {
        List<Student> students = studentRepository.findAll();
        int generated = 0;
        int skipped = 0;
        int failed = 0;

        for (Student student : students) {
            try {
                // Skip if already has QR code
                if (student.getQrCodeUrl() != null && !student.getQrCodeUrl().isEmpty()) {
                    skipped++;
                    continue;
                }

                // Generate QR code
                generateQRCodeForStudent(student);
                generated++;

            } catch (Exception e) {
                log.error("Failed to generate QR code for student: {}", student.getRollNumber(), e);
                failed++;
            }
        }

        log.info("QR code generation completed. Generated: {}, Skipped: {}, Failed: {}", generated, skipped, failed);
        return generated;
    }

    /**
     * Regenerate QR codes for all students (overwrites existing)
     */
    @Transactional
    public int regenerateQRCodesForAllStudents() {
        List<Student> students = studentRepository.findAll();
        int generated = 0;
        int failed = 0;

        for (Student student : students) {
            try {
                generateQRCodeForStudent(student);
                generated++;
            } catch (Exception e) {
                log.error("Failed to regenerate QR code for student: {}", student.getRollNumber(), e);
                failed++;
            }
        }

        log.info("QR code regeneration completed. Generated: {}, Failed: {}", generated, failed);
        return generated;
    }
}

