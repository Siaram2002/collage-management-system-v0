package com.cms.attendance.attendance_.controller;

import com.cms.attendance.attendance_.dto.AttendanceReportDTO;
import com.cms.attendance.attendance_.dto.AttendanceSummaryDTO;
import com.cms.attendance.attendance_.service.AttendanceReportService;
import com.cms.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance/reports")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttendanceReportService attendanceReportService;

    /**
     * Get attendance reports with filters
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAttendanceReports(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<AttendanceReportDTO> reports = attendanceReportService.getAttendanceReports(departmentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Attendance reports fetched successfully", reports));
    }

    /**
     * Get daily attendance summary
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long departmentId) {

        List<AttendanceSummaryDTO> summary = attendanceReportService.getDailyAttendanceSummary(date, departmentId);
        return ResponseEntity.ok(ApiResponse.success("Daily attendance summary fetched successfully", summary));
    }

    /**
     * Get monthly attendance summary
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long departmentId) {

        List<AttendanceSummaryDTO> summary = attendanceReportService.getMonthlyAttendanceSummary(year, month, departmentId);
        return ResponseEntity.ok(ApiResponse.success("Monthly attendance summary fetched successfully", summary));
    }

    /**
     * Get yearly attendance summary
     */
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse> getYearlySummary(
            @RequestParam int year,
            @RequestParam(required = false) Long departmentId) {

        List<AttendanceSummaryDTO> summary = attendanceReportService.getYearlyAttendanceSummary(year, departmentId);
        return ResponseEntity.ok(ApiResponse.success("Yearly attendance summary fetched successfully", summary));
    }

    /**
     * Get department class attendance summary
     */
    @GetMapping("/class/{departmentClassId}")
    public ResponseEntity<ApiResponse> getClassSummary(
            @PathVariable Long departmentClassId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        AttendanceSummaryDTO summary = attendanceReportService.getDepartmentClassSummary(departmentClassId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Class attendance summary fetched successfully", summary));
    }
}

