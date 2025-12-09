package com.cms.transport.driver.service;


import java.util.List;

import com.cms.students.models.Student;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.model.QRScanLog;

public interface QRScanLogService {

    /**
     * Create a new QR scan log entry
     * @param qrData Encrypted QR data from driver
     * @param driverId ID of the driver scanning
     * @return saved QRScanLog
     */
    QRScanLog createLog(String qrData, Long driverId) throws Exception;

    /**
     * Retrieve all scan logs
     */
    java.util.List<QRScanLog> getAllLogs();
    List<QRScanLog> getLogsByDriver(Long driverId);
}
