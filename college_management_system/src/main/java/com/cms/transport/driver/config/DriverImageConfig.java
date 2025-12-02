package com.cms.transport.driver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DriverImageConfig {

    /**
     * Base directory on the server to save driver images
     * Example: uploads/photos/drivers
     */
    @Value("${driver.images.base-dir:uploads/photos/drivers}")
    private String baseDir;

    /**
     * Base URL prefix to access driver images
     * Example: /photos/drivers
     */
    @Value("${driver.images.base-url:/photos/drivers}")
    private String baseUrl;

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public String getBaseDir() {
        return baseDir;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    // -------------------------------------------------------------------------
    // Optional helper method to build full URL for a file
    // -------------------------------------------------------------------------
    public String buildFileUrl(String fileName) {
        return baseUrl + "/" + fileName;
    }
}
