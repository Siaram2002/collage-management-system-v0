package com.cms.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.cms.config.QRFileStorageConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class QRCodeUtil {

	private final QRFileStorageConfig fileStorageConfig;

	/**
	 * Generates a QR code image for a given student roll number and saves it to the
	 * configured upload directory.
	 *
	 * @param studentRollNumber the student roll number or data to encode in the QR
	 *                          code
	 * @return the full URL of the generated QR image
	 */
	public String generateQRCode(String qrId) {
		if (qrId == null || qrId.isBlank()) {
			log.error("Student roll number is null or blank");
			throw new IllegalArgumentException("Student roll number cannot be null or empty");
		}

		String encryptedId;
		try {
			encryptedId = EncryptDecryptUtil.encrypt(qrId);
		} catch (Exception e) {
			log.error("Encryption failed for student {}", qrId, e);
			throw new RuntimeException("Failed to encrypt student data for QR code", e);
		}

		BitMatrix bitMatrix;
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			bitMatrix = qrCodeWriter.encode(encryptedId, BarcodeFormat.QR_CODE, 250, 250);
		} catch (WriterException e) {
			log.error("QR code generation failed for student {}", qrId, e);
			throw new RuntimeException("Failed to generate QR code", e);
		}

		Path uploadPath = Paths.get(fileStorageConfig.getQrUploadDir());
		try {
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				log.info("Created QR code directory: {}", uploadPath.toAbsolutePath());
			}
		} catch (IOException e) {
			log.error("Failed to create upload directory: {}", uploadPath.toAbsolutePath(), e);
			throw new RuntimeException("Could not create QR code upload directory", e);
		}

		String fileName = qrId + ".png";
		Path filePath = uploadPath.resolve(fileName);

		try {
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
			log.info("✅ QR code generated for student {} -> {}", qrId, filePath.toAbsolutePath());
		} catch (IOException e) {
			log.error("Failed to write QR code image to file {}", filePath.toAbsolutePath(), e);
			throw new RuntimeException("Failed to write QR code image to file", e);
		}

		try {
			// Construct accessible URL
			String baseUrl = fileStorageConfig.getBaseUrl();
			if (!baseUrl.endsWith("/"))
				baseUrl += "/";
			String relativePath = fileStorageConfig.getQrUploadDir().replace("\\", "/");
			if (!relativePath.endsWith("/"))
				relativePath += "/";
			return baseUrl + relativePath + fileName;
		} catch (Exception e) {
			log.error("Failed to construct QR code URL for student {}", qrId, e);
			throw new RuntimeException("Failed to construct QR code URL", e);
		}
	}
}
