package com.cms.students.services.media;

import com.cms.students.config.StudentQrImageConfig;
import com.cms.students.exceptions.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
public class QRFileStorageService {

    private final StudentQrImageConfig qrConfig;

    public QRFileStorageService(StudentQrImageConfig qrConfig) {
        this.qrConfig = qrConfig;
    }

    /**
     * Save QR code image for a student
     * Returns full URL to access QR image
     */
    public String storeStudentQr(String uniqueId, byte[] qrBytes) {

        Path targetDir = Paths.get(qrConfig.getStudentQrBaseDir());

        try {
            // Ensure folder exists
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Build filename: <uniqueId>.png
            String fileName = uniqueId + qrConfig.getQrFileExtension();
            Path qrFile = targetDir.resolve(fileName);

            // Write file
            Files.write(qrFile, qrBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Return URL: /qr/students/12345.png
            return qrConfig.getStudentQrBaseUrl() + "/" + fileName;

        } catch (IOException e) {
            log.error("Failed to store student QR {}", uniqueId, e);
            throw new FileStorageException("Failed to store student QR: " + e.getMessage());
        }
    }

    /**
     * Delete student QR
     */
    public boolean deleteStudentQr(String uniqueId) {

        Path filePath = Paths.get(
                qrConfig.getStudentQrBaseDir(),
                uniqueId + qrConfig.getQrFileExtension()
        );

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete student QR {}", uniqueId, e);
            return false;
        }
    }

    /**
     * Get the folder where student QR images are stored
     */
    public String getStudentQrFolderPath() {
        return qrConfig.getStudentQrBaseDir();
    }
}
