package com.cms.qr.service;

import com.cms.qr.QRUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QRService {

    private final QRUtils qrUtils;

    /**
     * Generate QR for an entity using:
     * ROLE-PREFIX + "-" + UNIQUE_ID
     *
     * Examples:
     *   STUDENT-ROLL001
     *   STAFF-STF22
     *   TEACHER-TCH12
     *   VISITOR-VST88
     */
    public byte[] generateQrForEntity(String rolePrefix, String uniqueId) {

        if (rolePrefix == null || rolePrefix.isBlank())
            throw new IllegalArgumentException("Role prefix cannot be empty");

        if (uniqueId == null || uniqueId.isBlank())
            throw new IllegalArgumentException("Unique ID cannot be empty");

        String prefix = rolePrefix.toUpperCase().trim();
        String encodedData = prefix + "-" + uniqueId;

        return qrUtils.generateQRCode(encodedData);
    }

    /**
     * Generate QR for entity with custom width & height
     */
    public byte[] generateQrForEntity(String rolePrefix,
                                      String uniqueId,
                                      int width,
                                      int height) {

        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Width/Height must be positive");

        if (rolePrefix == null || rolePrefix.isBlank())
            throw new IllegalArgumentException("Role prefix cannot be empty");

        if (uniqueId == null || uniqueId.isBlank())
            throw new IllegalArgumentException("Unique ID cannot be empty");

        String prefix = rolePrefix.toUpperCase().trim();
        String encodedData = prefix + "-" + uniqueId;

        return qrUtils.generateQRCode(encodedData, width, height);
    }
}
