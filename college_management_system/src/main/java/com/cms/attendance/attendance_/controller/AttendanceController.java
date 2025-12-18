package com.cms.attendance.attendance_.controller;


import com.cms.attendance.attendance_.dto.AttendanceScanRequestDTO;
import com.cms.attendance.attendance_.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/scan")
    public ResponseEntity<?> scanQr(@RequestBody AttendanceScanRequestDTO dto) {
        attendanceService.markAttendance(dto);
        return ResponseEntity.ok("Attendance marked successfully");
    }
}

