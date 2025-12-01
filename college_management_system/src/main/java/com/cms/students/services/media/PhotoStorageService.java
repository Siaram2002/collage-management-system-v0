package com.cms.students.services.media;

import com.cms.common.exceptions.PhotoStorageServiceException;
import com.cms.students.config.StudentImgConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoStorageService {

    private final StudentImgConfig studentImgConfig;

    /**
     * Store only Student Photo
     */
    public String storeStudentPhoto(String uniqueId, byte[] bytes) throws IOException {

        Path folder = Paths.get(studentImgConfig.getStudentImageBaseDir());

        try {
            // Ensure directory exists
            Files.createDirectories(folder);

            // Build file path <uniqueId>.png
            Path filePath = folder.resolve(studentImgConfig.buildFileName(uniqueId));

            // Save file
            try (var out = Files.newOutputStream(
                    filePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                out.write(bytes);
            }

            // Return URL path
            return studentImgConfig.getStudentImageBaseUrl() + "/" + filePath.getFileName();

        } catch (IOException e) {
            log.error("Failed to store student photo: {}", uniqueId, e);
            throw new PhotoStorageServiceException("Failed to store student photo", e);
        }
    }

    /**
     * Delete Student Photo
     */
    public boolean deleteStudentPhoto(String uniqueId) {

        Path filePath = Paths.get(
                studentImgConfig.getStudentImageBaseDir(),
                studentImgConfig.buildFileName(uniqueId)
        );

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete student photo {}", uniqueId, e);
            return false;
        }
    }
}
