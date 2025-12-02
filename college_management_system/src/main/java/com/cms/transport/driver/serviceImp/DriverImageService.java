package com.cms.transport.driver.serviceImp;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cms.transport.driver.config.DriverImageConfig;

import java.io.IOException;
import java.nio.file.*;

@Service
public class DriverImageService {

    private final DriverImageConfig driverImageConfig;

    public DriverImageService(DriverImageConfig driverImageConfig) {
        this.driverImageConfig = driverImageConfig;
    }

    /**
     * Store driver photo and return the file URL
     */
    public String storePhoto(Long driverId, MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) return null;

        String fileName = "driver_" + driverId + "_" + System.currentTimeMillis()
                + getExtension(photo.getOriginalFilename());

        Path uploadDir = Paths.get(driverImageConfig.getBaseDir());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return driverImageConfig.buildFileUrl(fileName);
    }

    /**
     * Update existing driver photo
     */
    public String updatePhoto(String existingPhotoUrl, Long driverId, MultipartFile photo) throws IOException {
        // Delete old photo
        if (existingPhotoUrl != null) {
            Path oldFile = Paths.get(driverImageConfig.getBaseDir(),
                    Paths.get(existingPhotoUrl).getFileName().toString());
            if (Files.exists(oldFile)) Files.delete(oldFile);
        }

        // Store new photo
        return storePhoto(driverId, photo);
    }

    /**
     * Delete photo from storage
     */
    public void deletePhoto(String photoUrl) throws IOException {
        if (photoUrl != null) {
            Path filePath = Paths.get(driverImageConfig.getBaseDir(),
                    Paths.get(photoUrl).getFileName().toString());
            if (Files.exists(filePath)) Files.delete(filePath);
        }
    }

    private String getExtension(String filename) {
        return filename != null && filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    }
}
