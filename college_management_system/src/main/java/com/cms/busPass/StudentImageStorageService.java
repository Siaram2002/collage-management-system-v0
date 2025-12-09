package com.cms.busPass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class StudentImageStorageService {

    // Local directory where student images are stored
    private static final String BASE_DIR = "buspass/students";
    
    // HTTP URL prefix to serve student images via Spring static resource mapping
    private static final String URL_PREFIX = "/buspass/students/";

    public StudentImageStorageService() {
        try {
            Path basePath = Paths.get(BASE_DIR);
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
                log.info("Created student image directory: {}", basePath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create student image directory", e);
            throw new RuntimeException("Failed to create student image directory", e);
        }
    }

    /**
     * Save or overwrite student image and return HTTP URL
     */
    public String saveStudentImage(String rollNumber, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new RuntimeException("Photo is empty");
        }

        try {
            String fileName = rollNumber + ".png";
            Path filePath = Paths.get(BASE_DIR, fileName);

            // Overwrite existing file
            Files.write(filePath, photo.getBytes());

            log.info("Stored student image for rollNumber={} at {}", rollNumber, filePath.toAbsolutePath());

            // Return HTTP URL accessible via Spring
            return getStudentImageUrl(rollNumber);

        } catch (IOException e) {
            log.error("Error storing student image for rollNumber={}", rollNumber, e);
            throw new RuntimeException("Failed to store student image", e);
        }
    }

    /**
     * Get Path to the stored image (for internal use)
     */
    public Path getStudentImagePath(String rollNumber) {
        return Paths.get(BASE_DIR, rollNumber + ".png");
    }

    /**
     * Get HTTP URL to access the student image
     */
    public String getStudentImageUrl(String rollNumber) {
        return URL_PREFIX + rollNumber + ".png";
    }
}
