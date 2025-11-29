package com.cms.config;



import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Getter
@Component
public class QRFileStorageConfig {

    @Value("${file.upload-dir}")
    private String qrUploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        File uploadDirectory = new File(qrUploadDir);
        if (!uploadDirectory.exists()) {
            boolean created = uploadDirectory.mkdirs();
            if (created) {
                log.info("✅ Created upload directory: {}", uploadDirectory.getAbsolutePath());
            } else {
                log.warn("⚠️ Failed to create upload directory: {}", uploadDirectory.getAbsolutePath());
            }
        } else {
            log.info("📁 Using existing upload directory: {}", uploadDirectory.getAbsolutePath());
        }

        log.info("🌍 FileStorageConfig initialized with Base URL: {}", baseUrl);
    }
}
