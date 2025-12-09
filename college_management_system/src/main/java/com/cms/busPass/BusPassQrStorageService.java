package com.cms.busPass;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class BusPassQrStorageService {

    // Local directory where QR codes are stored
    private static final String BASE_DIR = "buspass/qrImages";

    // HTTP URL prefix to serve QR codes via Spring static resource mapping
    private static final String URL_PREFIX = "/buspass/qrImages/";

    public BusPassQrStorageService() {
        try {
            Path basePath = Paths.get(BASE_DIR);
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
                log.info("Created QR code directory: {}", basePath.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Failed to create QR code directory", e);
            throw new RuntimeException("Failed to create QR code directory", e);
        }
    }

    /**
     * Save or overwrite QR code for a student roll number and return HTTP URL
     */
    public String saveQr(String busPassUid, String rollNumber) {
        if (rollNumber == null || rollNumber.isEmpty()) {
            throw new RuntimeException("Roll number cannot be null or empty");
        }

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(busPassUid, BarcodeFormat.QR_CODE, 300, 300);

            Path filePath = Paths.get(BASE_DIR, rollNumber + ".png");

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted old QR for rollNumber={}", rollNumber);
            }

            MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);
            log.info("Stored QR code for rollNumber={} at {}", rollNumber, filePath.toAbsolutePath());

            return getQrUrl(rollNumber);

        } catch (Exception e) {
            log.error("Error storing QR code for rollNumber={}", rollNumber, e);
            throw new RuntimeException("Failed to store QR code", e);
        }
    }

    /**
     * Get Path to the stored QR code (internal use)
     */
    public Path getQrFilePath(String rollNumber) {
        return Paths.get(BASE_DIR, rollNumber + ".png");
    }

    /**
     * Get HTTP URL to access the QR code
     */
    public String getQrUrl(String rollNumber) {
        return URL_PREFIX + rollNumber + ".png";
    }
}
