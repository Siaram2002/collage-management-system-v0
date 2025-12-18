package com.cms.attendance.attendance_.service;

import com.cms.attendance.attendance_.dto.AttendanceReportDTO;
import com.cms.attendance.attendance_.dto.AttendanceSummaryDTO;
import com.cms.attendance.attendance_.dto.StudentAttendanceSummaryDTO;
import com.cms.attendance.attendance_.module.Attendance;
import com.cms.attendance.attendance_.repositories.AttendanceRepository;
import com.cms.attendance.lecture.repositories.LectureRepository;
import com.cms.college.reporitories.DepartmentClassRepository;
import com.cms.college.reporitories.DepartmentRepository;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceReportService {

    private final AttendanceRepository attendanceRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentClassRepository departmentClassRepository;

    /**
     * Get attendance reports filtered by department, date range
     */
    public List<AttendanceReportDTO> getAttendanceReports(Long departmentId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances;
        
        if (departmentId != null) {
            attendances = attendanceRepository.findByDepartmentAndDateRange(departmentId, startDate, endDate);
        } else {
            attendances = attendanceRepository.findByDateRange(startDate, endDate);
        }

        return attendances.stream()
                .map(this::mapToReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get daily attendance summary for a specific date
     */
    public List<AttendanceSummaryDTO> getDailyAttendanceSummary(LocalDate date, Long departmentId) {
        List<Attendance> attendances;
        
        if (departmentId != null) {
            attendances = attendanceRepository.findByDepartmentAndDateRange(departmentId, date, date);
        } else {
            attendances = attendanceRepository.findByDateRange(date, date);
        }

        return buildSummaryByDepartmentClass(attendances, date, date);
    }

    /**
     * Get monthly attendance summary
     */
    public List<AttendanceSummaryDTO> getMonthlyAttendanceSummary(int year, int month, Long departmentId) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Attendance> attendances;
        if (departmentId != null) {
            attendances = attendanceRepository.findByDepartmentAndDateRange(departmentId, startDate, endDate);
        } else {
            attendances = attendanceRepository.findByDateRange(startDate, endDate);
        }

        return buildSummaryByDepartmentClass(attendances, startDate, endDate);
    }

    /**
     * Get yearly attendance summary
     */
    public List<AttendanceSummaryDTO> getYearlyAttendanceSummary(int year, Long departmentId) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Attendance> attendances;
        if (departmentId != null) {
            attendances = attendanceRepository.findByDepartmentAndDateRange(departmentId, startDate, endDate);
        } else {
            attendances = attendanceRepository.findByDateRange(startDate, endDate);
        }

        return buildSummaryByDepartmentClass(attendances, startDate, endDate);
    }

    /**
     * Get attendance summary for a specific department class
     */
    public AttendanceSummaryDTO getDepartmentClassSummary(Long departmentClassId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByDepartmentClassAndDateRange(
                departmentClassId, startDate, endDate);

        // Get all students in this class
        List<Student> students = studentRepository.findByDepartmentClass_DepartmentClassId(departmentClassId);
        
        // Get total lectures in date range for this class
        Long totalLecturesRaw = lectureRepository.countByDepartmentClass_DepartmentClassIdAndLectureDateBetween(
                departmentClassId, startDate, endDate);
        
        final Long totalLectures = (totalLecturesRaw == null) ? 0L : totalLecturesRaw;

        Map<Long, Long> studentAttendanceCount = attendances.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStudent().getStudentId(),
                        Collectors.counting()
                ));

        List<StudentAttendanceSummaryDTO> studentSummaries = students.stream()
                .map(student -> {
                    Long presentCount = studentAttendanceCount.getOrDefault(student.getStudentId(), 0L);
                    Long absentCount = totalLectures - presentCount;
                    double percentage = totalLectures > 0 ? (presentCount * 100.0 / totalLectures) : 0.0;

                    String status = percentage >= 75 ? "GOOD" : percentage >= 60 ? "WARNING" : "CRITICAL";

                    return StudentAttendanceSummaryDTO.builder()
                            .studentId(student.getStudentId())
                            .studentName(getFullName(student))
                            .rollNumber(student.getRollNumber())
                            .admissionNumber(student.getAdmissionNumber())
                            .totalLectures(totalLectures.intValue())
                            .presentCount(presentCount.intValue())
                            .absentCount(absentCount.intValue())
                            .attendancePercentage(percentage)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        Long totalPresent = studentAttendanceCount.values().stream().mapToLong(Long::longValue).sum();
        double overallPercentage = students.size() > 0 && totalLectures > 0 
                ? (totalPresent * 100.0 / (students.size() * totalLectures)) : 0.0;

        if (!students.isEmpty()) {
            Student firstStudent = students.get(0);
            var deptClass = firstStudent.getDepartmentClass();
            
            return AttendanceSummaryDTO.builder()
                    .departmentId(deptClass.getDepartment().getDepartmentId())
                    .departmentName(deptClass.getDepartment().getName())
                    .departmentCode(deptClass.getDepartment().getDepartmentCode())
                    .className(deptClass.getClassName())
                    .classCode(deptClass.getClassCode())
                    .totalLectures(totalLectures.intValue())
                    .totalStudents(students.size())
                    .presentCount(totalPresent.intValue())
                    .absentCount((students.size() * totalLectures.intValue()) - totalPresent.intValue())
                    .attendancePercentage(overallPercentage)
                    .studentSummaries(studentSummaries)
                    .build();
        }

        return null;
    }

    private List<AttendanceSummaryDTO> buildSummaryByDepartmentClass(List<Attendance> attendances, LocalDate startDate, LocalDate endDate) {
        Map<String, List<Attendance>> byDepartmentClass = attendances.stream()
                .collect(Collectors.groupingBy(a -> 
                    a.getLecture().getDepartmentClass().getDepartmentClassId().toString()));

        List<AttendanceSummaryDTO> summaries = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : byDepartmentClass.entrySet()) {
            Long departmentClassId = Long.parseLong(entry.getKey());
            AttendanceSummaryDTO summary = getDepartmentClassSummary(departmentClassId, startDate, endDate);
            if (summary != null) {
                summaries.add(summary);
            }
        }

        return summaries;
    }

    private AttendanceReportDTO mapToReportDTO(Attendance attendance) {
        var lecture = attendance.getLecture();
        var student = attendance.getStudent();
        var deptClass = lecture.getDepartmentClass();

        return AttendanceReportDTO.builder()
                .attendanceId(attendance.getAttendanceId())
                .studentId(student.getStudentId())
                .studentName(getFullName(student))
                .rollNumber(student.getRollNumber())
                .admissionNumber(student.getAdmissionNumber())
                .lectureId(lecture.getLectureId())
                .subjectName(lecture.getSubjectName())
                .lectureDate(lecture.getLectureDate())
                .facultyName(lecture.getFaculty().getFullName())
                .departmentName(deptClass.getDepartment().getName())
                .departmentCode(deptClass.getDepartment().getDepartmentCode())
                .className(deptClass.getClassName())
                .classCode(deptClass.getClassCode())
                .scanTime(attendance.getScanTime())
                .status(attendance.getStatus())
                .build();
    }

    private String getFullName(Student student) {
        String name = student.getFirstName();
        if (student.getMiddleName() != null && !student.getMiddleName().isEmpty()) {
            name += " " + student.getMiddleName();
        }
        if (student.getLastName() != null && !student.getLastName().isEmpty()) {
            name += " " + student.getLastName();
        }
        return name;
    }
}

