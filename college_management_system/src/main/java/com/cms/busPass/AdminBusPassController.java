package com.cms.busPass;

import com.cms.common.ApiResponse;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ADMIN-facing endpoints for managing / issuing bus passes on behalf of students
 * (e.g., students who don't have a phone / app access).
 *
 * NOTE: This controller is additive and does NOT modify existing student-facing APIs.
 */
@RestController
@RequestMapping("/api/admin/bus-pass")
@RequiredArgsConstructor
@Slf4j
public class AdminBusPassController {

    private final BusPassService busPassService;
    private final StudentRepository studentRepository;
    private final StudentBusPassRepository studentBusPassRepository;
    private final BusPassRepository busPassRepository;

    /**
     * Fetch student details + latest bus pass summary
     * using roll number or admission number.
     *
     * This is intended for the admin "Generate Bus Pass" screen,
     * to show the student and existing pass (if any) before issuing.
     */
    @GetMapping("/student")
    public ResponseEntity<ApiResponse> getStudentForBusPass(
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) String admissionNumber
    ) {
        log.info("Admin: fetching student for bus pass, rollNumber={}, admissionNumber={}",
                rollNumber, admissionNumber);

        if ((rollNumber == null || rollNumber.isBlank())
                && (admissionNumber == null || admissionNumber.isBlank())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("Either rollNumber or admissionNumber is required")
            );
        }

        try {
            // 1. Load student
            Student student = studentRepository.findByRollNumberAndAdmissionNumber(
                    rollNumber, admissionNumber
            ).orElseThrow(() -> new RuntimeException(
                    "Student not found with given identifiers"
            ));

            AdminBusPassStudentDTO.AdminBusPassStudentDTOBuilder builder =
                    AdminBusPassStudentDTO.builder()
                            .studentId(student.getStudentId())
                            .fullName((student.getFirstName() + " " +
                                    (student.getLastName() != null ? student.getLastName() : "")).trim())
                            .rollNumber(student.getRollNumber())
                            .admissionNumber(student.getAdmissionNumber())
                            .departmentCode(student.getDepartment() != null
                                    ? student.getDepartment().getDepartmentCode()
                                    : null)
                            .courseCode(student.getCourse() != null
                                    ? student.getCourse().getCourseCode()
                                    : null)
                            .transportEnabled(student.getTransportEnabled())
                            .studentPhotoUrl(student.getPhotoUrl());

            // 2. Try to fetch latest bus pass mapping
            studentBusPassRepository.findByRollNumberAndAdmissionNumber(
                    student.getRollNumber(), student.getAdmissionNumber()
            ).ifPresent(mapping -> {
                busPassRepository.findByBusPassUid(mapping.getBusPassUid())
                        .ifPresent(pass -> {
                            builder.currentBusPassUid(pass.getBusPassUid());
                            builder.currentStatus(pass.getStatus() != null ? pass.getStatus().name() : null);
                            builder.currentValidTill(pass.getValidTill());
                            builder.currentBusRoute(pass.getBusRoute());
                            builder.currentRouteCode(pass.getRouteCode());
                            builder.currentQrUrl(pass.getQrUrl());
                        });
            });

            return ResponseEntity.ok(
                    ApiResponse.success("Student fetched successfully", builder.build())
            );

        } catch (Exception e) {
            log.error("Error fetching student for bus pass", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Student not found"));
        }
    }

    /**
     * ADMIN: Issue or renew a bus pass for a student.
     *
     * This wraps the existing BusPassService.issueOrRenewBusPass() so we
     * REUSE the core logic. No photo upload is required here; we reuse
     * the student's existing photo (if available).
     */
    @PostMapping("/issue")
    public ResponseEntity<ApiResponse> adminIssueOrRenewBusPass(
            @RequestBody AdminBusPassIssueRequest adminRequest
    ) {
        log.info("Admin: issuing/renewing bus pass for rollNumber={}, admissionNumber={}",
                adminRequest.getRollNumber(), adminRequest.getAdmissionNumber());

        try {
            if ((adminRequest.getRollNumber() == null || adminRequest.getRollNumber().isBlank())
                    && (adminRequest.getAdmissionNumber() == null || adminRequest.getAdmissionNumber().isBlank())) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.fail("Either rollNumber or admissionNumber is required")
                );
            }

            // Map admin request to existing BusPassRequest (no photo)
            BusPassRequest request = BusPassRequest.builder()
                    .rollNumber(adminRequest.getRollNumber())
                    .admissionNumber(adminRequest.getAdmissionNumber())
                    .busRoute(adminRequest.getBusRoute())
                    .feePaid(adminRequest.getFeePaid())
                    .validityInMonths(adminRequest.getValidityInMonths())
                    .routeCode(adminRequest.getRouteCode())
                    .build();

            BusPass busPass = busPassService.issueOrRenewBusPass(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Bus pass issued/renewed successfully", busPass)
            );

        } catch (Exception e) {
            log.error("Error issuing bus pass by admin", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Failed to issue/renew bus pass: " + e.getMessage()));
        }
    }
}


