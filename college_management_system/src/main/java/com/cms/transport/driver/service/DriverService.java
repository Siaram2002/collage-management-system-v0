package com.cms.transport.driver.service;

import com.cms.transport.driver.dto.ScanResultDTO;
import com.cms.transport.driver.model.Driver;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DriverService {


    public Driver createDriverWithPhoto(Driver driver, MultipartFile photo) throws IOException;
	public Driver updateDriverPhoto(Long driverId, MultipartFile photo) throws IOException ;

    Driver updateDriver(Long driverId, Driver updatedDriver);

    Driver getDriverById(Long driverId);

    void deleteDriver(Long driverId);

    List<Driver> getAllDrivers();

    List<Driver> searchDrivers(String keyword);

    Driver updateStatus(Long driverId, String status);

    Driver updateLicenseDetails(Long driverId, String licenseNumber, LocalDate expiry);

    Driver updatePhoto(Long driverId, MultipartFile photo) throws Exception;

    Driver assignContact(Long driverId, Long contactId);

    Driver assignAddress(Long driverId, Long addressId);

    Driver assignUser(Long driverId, Long userId);

    List<Driver> getDriversWithExpiredLicense();

    void deleteAllDrivers();
	ScanResultDTO driverScanResult(String qrData) throws Exception;
}
