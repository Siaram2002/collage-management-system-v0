package com.cms.attendance.faculty.service;

import com.cms.attendance.faculty.dto.FacultyProfileDTO;
import com.cms.attendance.faculty.dto.FacultyRequestDTO;
import com.cms.attendance.faculty.dto.FacultyResponseDTO;
import com.cms.attendance.faculty.module.Faculty;
import com.cms.attendance.faculty.repositories.FacultyRepository;
import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.Department;
import com.cms.college.models.User;
import com.cms.college.reporitories.ContactRepository;
import com.cms.college.reporitories.DepartmentRepository;
import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;
import com.cms.common.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final ContactRepository contactRepository;
    private final CommonUserService commonUserService;
    private final PasswordEncoder passwordEncoder;

    private static final String FACULTY_PHOTO_DIR = "uploads/faculty/";
    private static final RoleEnum FACULTY_ROLE = RoleEnum.FACULTY;

    // ================= CREATE FACULTY =================
    @Transactional
    public FacultyProfileDTO createFaculty(FacultyRequestDTO dto, MultipartFile photo) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is mandatory for faculty");
        }
        if (dto.getDepartmentId() == null) {
            throw new RuntimeException("Department ID is required");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        String facultyCode = generateFacultyCode(department);

        // ---------- Address ----------
        Address address = Address.builder()
                .line1(dto.getAddressLine1())
                .line2(dto.getAddressLine2())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .state(dto.getState())
                .country(dto.getCountry())
                .pin(dto.getPin())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        // ---------- Contact ----------
        Contact contact = Contact.builder()
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .altPhone(dto.getAltPhone())
                .address(address)
                .build();

        contact = contactRepository.save(contact);

        // ---------- Faculty ----------
        Faculty faculty = Faculty.builder()
                .facultyCode(facultyCode)
                .fullName(dto.getFullName())
                .dob(LocalDate.parse(dto.getDob()))
                .designation(dto.getDesignation())
                .status(dto.getStatus() != null ? dto.getStatus() : Status.ACTIVE.name())
                .department(department)
                .contact(contact)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Faculty savedFaculty = facultyRepository.save(faculty);

        // ---------- User Creation ----------
        User user = commonUserService.createUser(
                facultyCode,
                FACULTY_ROLE,
                savedFaculty.getFacultyId(),
                facultyCode + "@123",
                contact
        );

        savedFaculty.setUser(user);

        // ---------- Photo Handling ----------
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = saveFacultyPhoto(photo, facultyCode);
            savedFaculty.setPhotoUrl(photoUrl);
        }

        savedFaculty = facultyRepository.save(savedFaculty);

        log.info("Faculty created: {} (User ID: {})", facultyCode, user.getUserId());
        return mapToProfileDTO(savedFaculty);
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<FacultyResponseDTO> getAllFaculty() {
        return facultyRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public FacultyResponseDTO getFacultyById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        return mapToResponseDTO(faculty);
    }

    // ================= PHOTO SAVE =================
    private String saveFacultyPhoto(MultipartFile photo, String facultyCode) {
        try {
            Files.createDirectories(Paths.get(FACULTY_PHOTO_DIR));
            String fileName = facultyCode + ".png";
            Path filePath = Paths.get(FACULTY_PHOTO_DIR + fileName);
            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return FACULTY_PHOTO_DIR + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload faculty photo", e);
        }
    }

    // ================= PROFILE DTO =================
    private FacultyProfileDTO mapToProfileDTO(Faculty faculty) {
        Contact c = faculty.getContact();
        Address a = c != null ? c.getAddress() : null;

        return FacultyProfileDTO.builder()
                .facultyId(faculty.getFacultyId())
                .facultyCode(faculty.getFacultyCode())
                .fullName(faculty.getFullName())
                .dob(faculty.getDob())
                .designation(faculty.getDesignation())
                .status(faculty.getStatus())
                .departmentId(faculty.getDepartment().getDepartmentId())
                .email(c != null ? c.getEmail() : null)
                .phone(c != null ? c.getPhone() : null)
                .altPhone(c != null ? c.getAltPhone() : null)
                .addressLine1(a != null ? a.getLine1() : null)
                .addressLine2(a != null ? a.getLine2() : null)
                .city(a != null ? a.getCity() : null)
                .district(a != null ? a.getDistrict() : null)
                .state(a != null ? a.getState() : null)
                .country(a != null ? a.getCountry() : null)
                .pin(a != null ? a.getPin() : null)
                .latitude(a != null ? a.getLatitude() : null)
                .longitude(a != null ? a.getLongitude() : null)
                .photoUrl(faculty.getPhotoUrl())
                .build();
    }

    // ================= RESPONSE DTO (FULL DATA) =================
    private FacultyResponseDTO mapToResponseDTO(Faculty faculty) {
        Contact contact = faculty.getContact();
        Address address = contact != null ? contact.getAddress() : null;

        return FacultyResponseDTO.builder()
                .facultyId(faculty.getFacultyId())
                .facultyCode(faculty.getFacultyCode())
                .fullName(faculty.getFullName())
                .dob(faculty.getDob())
                .designation(faculty.getDesignation())
                .status(faculty.getStatus())
                .departmentId(faculty.getDepartment().getDepartmentId())
                .email(contact != null ? contact.getEmail() : null)
                .phone(contact != null ? contact.getPhone() : null)
                .altPhone(contact != null ? contact.getAltPhone() : null)
                .addressLine1(address != null ? address.getLine1() : null)
                .addressLine2(address != null ? address.getLine2() : null)
                .city(address != null ? address.getCity() : null)
                .district(address != null ? address.getDistrict() : null)
                .state(address != null ? address.getState() : null)
                .country(address != null ? address.getCountry() : null)
                .pin(address != null ? address.getPin() : null)
                .latitude(address != null ? address.getLatitude() : null)
                .longitude(address != null ? address.getLongitude() : null)
                .photoUrl(faculty.getPhotoUrl())
                .build();
    }

    // ================= FACULTY CODE GENERATION =================
    private String generateFacultyCode(Department department) {
        long count = facultyRepository.count() + 1;
        return "FLT-" + department.getDepartmentCode() + "-" + String.format("%03d", count);
    }

    // ================= LOGIN FACULTY =================
    @Transactional(readOnly = true)
    public FacultyProfileDTO loginFaculty(String username, String password) {

        // 1. User fetch
        User user = commonUserService.getUserByUsername(username);

        // 2. Password verify
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 3. Role check
        if (user.getRole() != RoleEnum.FACULTY) {
            throw new RuntimeException("Access denied: Not a faculty");
        }

        // 4. Faculty fetch using referenceId
        Long facultyId = user.getReferenceId();

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        // 5. Return full profile
        return mapToProfileDTO(faculty);
    }
}
