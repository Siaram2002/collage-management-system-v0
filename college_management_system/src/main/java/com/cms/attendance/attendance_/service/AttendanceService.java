package com.cms.attendance.attendance_.service;


import com.cms.attendance.attendance_.module.Attendance;
import com.cms.attendance.attendance_.dto.AttendanceScanRequestDTO;
import com.cms.attendance.attendance_.repositories.AttendanceRepository;
import com.cms.attendance.comman.enums.LectureStatus;
import com.cms.attendance.lectureQrCode.module.LectureQR;
import com.cms.attendance.lecture.module.Lecture;
import com.cms.attendance.lectureQrCode.repositories.LectureQrRepository;
import com.cms.notifications.service.NotificationService;
import com.cms.students.models.Student;

import com.cms.students.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttendanceService {

    private final LectureQrRepository lectureQrRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final NotificationService notificationService;

    public void markAttendance(AttendanceScanRequestDTO dto) {

        // 1️⃣ Validate QR
        LectureQR qr = lectureQrRepository.findByQrPayload(dto.getQrPayload())
                .orElseThrow(() -> new RuntimeException("Invalid QR"));

        if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("QR expired");
        }

        // 2️⃣ Lecture validation
        Lecture lecture = qr.getLecture();
        if (lecture.getStatus() != LectureStatus.ACTIVE) {
            throw new RuntimeException("Lecture not active");
        }

        // 3️⃣ Student validation
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getDepartmentClass() == null || lecture.getDepartmentClass() == null) {
            throw new RuntimeException("Student or Lecture department class not assigned");
        }

        if (!student.getDepartmentClass().getDepartmentClassId()
                .equals(lecture.getDepartmentClass().getDepartmentClassId())) {
            throw new RuntimeException("Student not belongs to this class");
        }

        // 4️⃣ Duplicate attendance block
        if (attendanceRepository.existsByLecture_LectureIdAndStudent_StudentId(
                lecture.getLectureId(),
                dto.getStudentId())) {
            throw new RuntimeException("Attendance already marked");
        }

        // 5️⃣ Save attendance
        Attendance attendance = new Attendance();
        attendance.setLecture(lecture);
        attendance.setStudent(student);
        attendance.setScanTime(LocalDateTime.now());
        attendance.setStatus("PRESENT");

        attendanceRepository.save(attendance);

        // 6️⃣ Send notification
        String studentName = student.getFirstName() + 
                (student.getLastName() != null ? " " + student.getLastName() : "");
        String message = String.format("Student %s (%s) marked attendance for %s - %s",
                studentName, student.getRollNumber(), lecture.getSubjectName(),
                lecture.getDepartmentClass().getClassName());
        
        try {
            notificationService.createNotification(
                    "Attendance Marked",
                    message,
                    "SUCCESS",
                    "ATTENDANCE",
                    student.getStudentId(),
                    "STUDENT"
            );
            log.info("Notification sent for attendance scan: studentId={}, lectureId={}", 
                    student.getStudentId(), lecture.getLectureId());
        } catch (Exception e) {
            log.error("Failed to send notification for attendance scan", e);
            // Don't fail the attendance marking if notification fails
        }
    }
}

