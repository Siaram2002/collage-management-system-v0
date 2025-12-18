package com.cms.transport.driver.service;


import java.util.List;

import com.cms.students.models.Student;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.model.QRScanLog;
import com.cms.transport.driver.dto.QRScanLogTableDTO;
import com.cms.transport.driver.dto.QRScanLogTableDTO;


public interface QRScanLogService {

    /**
     * Create a new QR scan log entry
     * @param studentRoll Student roll number from QR code
     * @param driverId ID of the driver scanning
     * @return saved QRScanLog
     */
    QRScanLog createLog(String studentRoll, Long driverId) throws Exception;

    /**
     * Create a new QR scan log entry with location data
     * @param studentRoll Student roll number from QR code
     * @param driverId ID of the driver scanning
     * @param scanStatus Status of the scan (VALID, INVALID, EXPIRED)
     * @param latitude Latitude of scan location
     * @param longitude Longitude of scan location
     * @param locationAddress Address of scan location
     * @param notes Additional notes
     * @return saved QRScanLog
     */
    QRScanLog createLog(String studentRoll, Long driverId, String scanStatus, 
                       Double latitude, Double longitude, String locationAddress, String notes) throws Exception;

    /**
     * Retrieve all scan logs
     */
    java.util.List<QRScanLog> getAllLogs();
    List<QRScanLog> getLogsByDriver(Long driverId);

    List<QRScanLogTableDTO> getAllLogReports();

}




