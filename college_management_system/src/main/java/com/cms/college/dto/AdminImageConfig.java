package com.cms.college.dto;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminImageConfig {

    /**
     * Base directory on the server to save admin images
     * Default: uploads/photos/admins
     */
    @Value("${admin.images.base-dir:uploads/photos/admins}")
    private String baseDir;

    /**
     * Base URL prefix to access admin images
     * Default: /photos/admins
     */
    @Value("${admin.images.url-prefix:/photos/admins}")
    private String urlPrefix;

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public String getBaseDir() {
        return baseDir;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    // -------------------------------------------------------------------------
    // Helper method to build full URL for a file
    // -------------------------------------------------------------------------
    public String buildFileUrl(String fileName) {
        return urlPrefix + "/" + fileName;
    }
}
