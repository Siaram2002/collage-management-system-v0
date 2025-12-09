package com.cms.college.dto;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class AdminImageService {

    private final AdminImageConfig adminImageConfig;

    public AdminImageService(AdminImageConfig adminImageConfig) {
        this.adminImageConfig = adminImageConfig;
    }

    /**
     * Store admin photo and return the file URL
     */
    public String storePhoto(Long adminId, MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) return null;

        String fileName = "admin_" + adminId + "_" + System.currentTimeMillis()
                + getExtension(photo.getOriginalFilename());

        Path uploadDir = Paths.get(adminImageConfig.getBaseDir());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return adminImageConfig.buildFileUrl(fileName);
    }

    /**
     * Update existing admin photo
     */
    public String updatePhoto(String existingPhotoUrl, Long adminId, MultipartFile photo) throws IOException {
        // Delete old photo
        if (existingPhotoUrl != null) {
            Path oldFile = Paths.get(adminImageConfig.getBaseDir(),
                    Paths.get(existingPhotoUrl).getFileName().toString());
            if (Files.exists(oldFile)) Files.delete(oldFile);
        }

        // Store new photo
        return storePhoto(adminId, photo);
    }

    /**
     * Delete photo from storage
     */
    public void deletePhoto(String photoUrl) throws IOException {
        if (photoUrl != null) {
            Path filePath = Paths.get(adminImageConfig.getBaseDir(),
                    Paths.get(photoUrl).getFileName().toString());
            if (Files.exists(filePath)) Files.delete(filePath);
        }
    }

    private String getExtension(String filename) {
        return filename != null && filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    }
}
