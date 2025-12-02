package com.cms.transport.driver.service;

import org.springframework.web.multipart.MultipartFile;

import com.cms.transport.driver.dto.DriverRegisterRequest;

import java.util.Map;

// Service interface defining driver registration functionality
public interface DriverService {

    /**
     * Registers a new driver or returns existing driver info.
     * @param req Driver registration request
     * @return Map with status, message, and driver data
     */
    Map<String, Object> registerDriver(DriverRegisterRequest req);


//    String uploadDriverExcel(MultipartFile file);

    // Bulk upload should be exposed
    Map<String, Object> bulkUploadDriversExcel(MultipartFile file);
}

