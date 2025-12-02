package com.cms.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class QRUtils {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES";

    @Value("${qr.secret-key:12345678901234567890123456789012}")
    private String secretKey;

    @Value("${qr.width:250}")
    private int defaultWidth;

    @Value("${qr.height:250}")
    private int defaultHeight;

    /**
     * Generate QR using default width/height
     */
    public byte[] generateQRCode(String entityData) {
        return generateQRCode(entityData, defaultWidth, defaultHeight);
    }

    /**
     * Generate QR using custom width/height
     */
    public byte[] generateQRCode(String entityData, int width, int height) {

        if (entityData == null || entityData.isBlank()) {
            throw new IllegalArgumentException("QR data cannot be null or empty");
        }

        try {
            String encrypted = encryptAES(entityData);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix =
                    qrCodeWriter.encode(entityData, BarcodeFormat.QR_CODE, width, height);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                return baos.toByteArray();
            }

        } catch (WriterException e) {
            log.error("QR generation error", e);
            throw new RuntimeException("QR generation failed", e);
        } catch (Exception e) {
            log.error("QR encryption/write error", e);
            throw new RuntimeException("QR processing failed", e);
        }
    }

    private String encryptAES(String plain) throws Exception {
        SecretKeySpec keySpec =
                new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decryptAES(String encryptedText) throws Exception {
        SecretKeySpec keySpec =
                new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
