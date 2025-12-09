package com.cms.auth;

import com.cms.auth.dto.*;
import com.cms.college.models.User;
import com.cms.college.models.AdminMaster;
import com.cms.college.services.AdminMasterService;
import com.cms.common.ApiResponse;
import com.cms.common.CommonUserService;
import com.cms.security.JwtUtils;
import com.cms.security.TokenBlacklistService;
import com.cms.students.models.Student;
import com.cms.students.services.StudentService;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.service.DriverService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    private final StudentService studentService;
    private final DriverService driverService;
    private final AdminMasterService adminService;
    private final CommonUserService commonUserService;

    // -------------------------
    //        LOGIN API
    // -------------------------
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {

        log.info("Login attempt for username: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            // Generate token
            String token = jwtUtils.generateToken(request.getUsername());
            log.info("JWT generated for username: {}", request.getUsername());

            User user = commonUserService.getUserByUsername(request.getUsername());
            String role = user.getRole().name();

            Object payload;

            switch (role) {

                case "STUDENT":
                    Student student = studentService.getStudentById(user.getReferenceId());

                    payload = StudentLoginPayload.builder()
                            .studentId(student.getStudentId())
                            .firstName(student.getFirstName())
                            .lastName(student.getLastName())
                            .rollNumber(student.getRollNumber())
                            .admissionNumber(student.getAdmissionNumber())
                            .courseName(student.getCourse().getCourseCode())
                            .departmentName(student.getDepartment().getDepartmentCode())
                            .photoUrl(student.getPhotoUrl())
                            .email(student.getContact().getEmail())
                            .phone(student.getContact().getPhone())
                            .role("STUDENT")
                            .bloodGroup(student.getBloodGroup())
                            .build();
                    break;

                case "DRIVER":
                    Driver driver = driverService.getDriverById(user.getReferenceId());

                    payload = DriverLoginPayload.builder()
                            .driverId(driver.getDriverId())
                            .fullName(driver.getFullName())
                            .licenseNumber(driver.getLicenseNumber())
                            .licenseExpiryDate(driver.getLicenseExpiryDate().toString())
                            .status(driver.getStatus().name())
                            .email(driver.getContact() != null ? driver.getContact().getEmail() : null)
                            .phone(driver.getContact() != null ? driver.getContact().getPhone() : null)
                            .photoUrl(driver.getPhotoUrl())
                            .role("DRIVER")
                            .build();
                    break;

                case "ADMIN":
                    AdminMaster admin = adminService.getAdminById(user.getReferenceId());

                    payload = AdminLoginPayload.builder()
                            .adminId(admin.getId())
                            .fullName(admin.getFullName())
                            .email(admin.getContact().getEmail())
                            .phone(admin.getContact().getPhone())
                            .photoUrl(admin.getPhotoUrl())
                            .status(admin.getAdminStatus().name())
                            .role("ADMIN")
                            .build();
                    break;

                default:
                    log.error("Unsupported role encountered: {}", role);
                    return ApiResponse.fail("Unsupported user role: " + role);
            }

            LoginResponse loginResponse = new LoginResponse(token, payload);

            log.info("Login successful for username: {} with role: {}", request.getUsername(), role);

            return ApiResponse.success("Login successful", loginResponse);

        } catch (AuthenticationException ex) {
            log.warn("Invalid login credentials for username: {}", request.getUsername());
            return ApiResponse.fail("Invalid username or password");

        } catch (Exception ex) {
            log.error("Login error: {}", ex.getMessage(), ex);
            return ApiResponse.fail("Something went wrong while logging in");
        }
    }

    // -------------------------
    //        LOGOUT API
    // -------------------------
    @PostMapping("/logout")
    public ApiResponse logout(@RequestHeader("Authorization") String authHeader) {

        log.info("Logout endpoint triggered");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid or missing Authorization header");
                return ApiResponse.fail("Token missing or invalid");
            }

            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);

            log.info("Token invalidated successfully");

            return ApiResponse.success("Logged out successfully");

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return ApiResponse.fail("Logout failed");
        }
    }
}
