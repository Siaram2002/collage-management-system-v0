package com.cms.transport.driver.serviceImp;



import com.cms.transport.driver.dto.QRScanLogTableDTO;
import com.cms.transport.driver.repository.DriverRepository;

import com.cms.transport.driver.repository.QRScanLogRepository;
import com.cms.transport.driver.service.QRScanLogService;
import com.cms.transport.models.TransportAssignment;
import com.cms.students.services.StudentService;
import com.cms.students.models.Student;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.model.QRScanLog;
import com.cms.transport.driver.dto.QRScanLogTableDTO;
import com.cms.transport.driver.mapper.QRScanLogTableMapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QRScanLogServiceImpl implements QRScanLogService {

    private final QRScanLogRepository logRepository;
    private final DriverRepository driverRepository;
    private final StudentService studentService;


    @Override
    public QRScanLog createLog(String studentRoll, Long driverId) throws Exception {
        return createLog(studentRoll, driverId, "VALID", null, null, null, null);
    }

    @Override
    public QRScanLog createLog(String studentRoll, Long driverId, String scanStatus, 
                              Double latitude, Double longitude, String locationAddress, String notes) throws Exception {
        if (studentRoll == null || studentRoll.trim().isEmpty()) {
            throw new IllegalArgumentException("Student roll number is required");
        }

        if (driverId == null) {
            throw new IllegalArgumentException("Driver ID is required");
        }

        // 1️⃣ Fetch student by roll number
        Student student = studentService.getStudentByRollNumber(studentRoll.trim());
        if (student == null) {
            throw new RuntimeException("Student not found for roll number: " + studentRoll);
        }

        // 2️⃣ Fetch driver by ID
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found for ID: " + driverId));

        // 3️⃣ Get assignment (bus and route) if assigned
        var assignment = driver.getAssignment();

        // 4️⃣ Create and save log entry
        QRScanLog logEntry = QRScanLog.builder()
                .student(student)
                .driver(driver)
                .bus(assignment != null ? assignment.getBus() : null)
                .route(assignment != null ? assignment.getRoute() : null)
                .scanStatus(scanStatus != null ? scanStatus : "VALID")
                .latitude(latitude)
                .longitude(longitude)
                .locationAddress(locationAddress)
                .notes(notes)
                .build();

        return logRepository.save(logEntry);
    }



    @Override
    public java.util.List<QRScanLog> getAllLogs() {
        return logRepository.findAllWithRelations();
    }






    @Override
    public List<QRScanLog> getLogsByDriver(Long driverId) {
        return logRepository.findByDriver_DriverIdOrderByScannedAtDesc(driverId);
    }

//    @Override
//    public List<QRScanLogTableDTO> getAllLogReports() {
//        return List.of();
//    }


    @Override
    public List<QRScanLogTableDTO> getAllLogReports() {

        return logRepository.findAllWithRelations()
                .stream()
                .map(QRScanLogTableMapper::toDTO)
                .toList();
    }

}
