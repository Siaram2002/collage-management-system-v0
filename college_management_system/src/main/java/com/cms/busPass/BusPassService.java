package com.cms.busPass;

import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class BusPassService {

    private final BusPassRepository busPassRepository;
    private final StudentBusPassRepository studentBusPassRepository;
    private final StudentRepository studentRepository; // added
    private final BusPassQrStorageService qrStorage;
    private final StudentImageStorageService studentImageStorageService;

    /**
     * ISSUE OR RENEW BUS PASS
     * Single method handles both new issuance and renewal
     */
    @Transactional
    public BusPass issueOrRenewBusPass(BusPassRequest request) {

        // 1. Validate student exists in student database
        Student student = studentRepository.findByRollNumberAndAdmissionNumber(
                request.getRollNumber(), request.getAdmissionNumber()
        ).orElseThrow(() -> new RuntimeException(
                "Student not found with rollNumber=" + request.getRollNumber() +
                        " and admissionNumber=" + request.getAdmissionNumber()
        ));

        // 2. Expire old pass if exists
        studentBusPassRepository.findByRollNumberAndAdmissionNumber(
                student.getRollNumber(), student.getAdmissionNumber()
        ).ifPresent(mapping -> {
            busPassRepository.findByBusPassUid(mapping.getBusPassUid())
                    .ifPresent(oldPass -> {
                        oldPass.setStatus(BusPassStatus.EXPIRED);
                        busPassRepository.save(oldPass);
                        log.info("Expired old bus pass UID={}", oldPass.getBusPassUid());
                    });
        });

        // 3. Compute validity based on months from frontend
        int months = request.getValidityInMonths() == null ? 12 : request.getValidityInMonths();
        LocalDateTime validTillDate = LocalDateTime.now().plusMonths(months);

        // 4. Create new bus pass
        BusPass busPass = BusPass.builder()
                .rollNumber(student.getRollNumber())
                .admissionNumber(student.getAdmissionNumber())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .course(student.getCourse() != null ? student.getCourse().getCourseCode() : null)
                .department(student.getDepartment() != null ? student.getDepartment().getDepartmentCode() : null)
                .busRoute(request.getBusRoute())
                .routeCode(request.getRouteCode())
                .feePaid(new BigDecimal(request.getFeePaid()))
                .issuedAt(LocalDateTime.now())
                .validTill(validTillDate)
                .status(BusPassStatus.ACTIVE)
                .busPassUid(UUID.randomUUID().toString())  // ✅ set here
                .build();


        busPass = busPassRepository.save(busPass);

        // 5. Save or overwrite student photo
        MultipartFile studentPhoto = request.getStudentPhoto();
        if (studentPhoto != null && !studentPhoto.isEmpty()) {
            String photoUrl = studentImageStorageService.saveStudentImage(student.getRollNumber(), studentPhoto);
            busPass.setStudentPhotoUrl(photoUrl);
        } else {
            // If no new photo provided (e.g. admin flow), reuse existing student photo if available
            if (student.getPhotoUrl() != null && !student.getPhotoUrl().isBlank()) {
                busPass.setStudentPhotoUrl(student.getPhotoUrl());
            }
        }

        // 6. Generate QR code containing busPassUid
        String qrUrl = qrStorage.saveQr(busPass.getBusPassUid(),student.getRollNumber());
        busPass.setQrUrl(qrUrl);

        busPassRepository.save(busPass);

        // 7. Update latest student → busPass mapping
        updateStudentBusPassMapping(busPass);

        // 8. Mark student as transport-enabled (used by frontend "transport management")
        if (Boolean.FALSE.equals(student.getTransportEnabled())) {
            student.setTransportEnabled(true);
            studentRepository.save(student);
        }

        log.info("Issued new bus pass UID={} for rollNumber={}", busPass.getBusPassUid(), busPass.getRollNumber());
        return busPass;
    }

    /**
     * QR SCAN → FETCH BUS PASS + STUDENT DETAILS
     */
    public BusPass scanBusPass(String busPassUid) {

    	System.out.println(busPassUid);

        return busPassRepository.findByBusPassUid(busPassUid)
                .orElseThrow(() -> new RuntimeException("Invalid or expired bus pass UID"));


    }

    /**
     * GET latest bus pass by roll number OR admission number
     */
    public BusPass getLatestBusPassForStudent(String rollNumber, String admissionNumber) {
        // First, find mapping from student_bus_pass table
        StudentBusPass mapping = studentBusPassRepository.findByRollNumberOrAdmissionNumber(
                rollNumber, admissionNumber
        ).orElseThrow(() -> new RuntimeException(
                "No active bus pass found for rollNumber=" + rollNumber +
                        " or admissionNumber=" + admissionNumber
        ));

        // Fetch actual bus pass record
        return busPassRepository.findByBusPassUid(mapping.getBusPassUid())
                .orElseThrow(() -> new RuntimeException(
                        "Bus pass not found for UID=" + mapping.getBusPassUid()
                ));
    }

    /**
     * UPDATE student_bus_pass table → always points to latest active pass
     */
    private void updateStudentBusPassMapping(BusPass busPass) {
        studentBusPassRepository.findByRollNumberAndAdmissionNumber(
                busPass.getRollNumber(), busPass.getAdmissionNumber()
        ).ifPresentOrElse(existing -> {
            // update old mapping
            existing.setBusPassUid(busPass.getBusPassUid());
            studentBusPassRepository.save(existing);
        }, () -> {
            // new mapping
            StudentBusPass newMapping = StudentBusPass.builder()
                    .rollNumber(busPass.getRollNumber())
                    .admissionNumber(busPass.getAdmissionNumber())
                    .busPassUid(busPass.getBusPassUid())
                    .build();
            studentBusPassRepository.save(newMapping);
        });
    }
    
    @Transactional
    public BusPass updateBusPassStatusByRollNumber(String rollNumber, BusPassStatus newStatus) {

        // 1. Find the latest mapping using roll number
        StudentBusPass mapping = studentBusPassRepository
                .findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException(
                        "No bus pass found for roll number = " + rollNumber));

        // 2. Fetch actual bus pass from the UID
        BusPass busPass = busPassRepository.findByBusPassUid(mapping.getBusPassUid())
                .orElseThrow(() -> new RuntimeException(
                        "Bus pass not found for UID = " + mapping.getBusPassUid()));

        // 3. Update status
        busPass.setStatus(newStatus);

        // 4. Save
        busPassRepository.save(busPass);

        log.info("Bus pass UID={} for rollNumber={} updated to status={}",
                busPass.getBusPassUid(), rollNumber, newStatus);

        return busPass;
    }

}
