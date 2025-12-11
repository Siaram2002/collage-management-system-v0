package com.cms.busPass;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bus-pass")
@RequiredArgsConstructor
public class BusPassController {

    private final BusPassService busPassService;

    /**
     * STUDENT: Issue or renew bus pass
     * Accepts multipart file for student photo
     */
    @PostMapping("/issue")
    public ResponseEntity<BusPass> issueOrRenewBusPass(
            @RequestPart("data") BusPassRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile studentPhoto
    ) {
        request.setStudentPhoto(studentPhoto);
        BusPass busPass = busPassService.issueOrRenewBusPass(request);
        return ResponseEntity.ok(busPass);
    }

    /**
     * STUDENT: Get bus pass by roll number or admission number
     */
    @GetMapping("/getPass")
    public ResponseEntity<BusPass> getStudentBusPass(
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) String admissionNumber
    ) {
        if ((rollNumber == null || rollNumber.isBlank()) &&
            (admissionNumber == null || admissionNumber.isBlank())) {
            return ResponseEntity.badRequest()
                    .body(null); // or return a custom error message
        }

        BusPass busPass = busPassService.getLatestBusPassForStudent(rollNumber, admissionNumber);
        return ResponseEntity.ok(busPass);
    }
    
    
    /**
     * QR SCAN → FETCH BUS PASS + STUDENT DETAILS
     * Example URL: GET /api/buspass/scan?uid=12345
     */
    @GetMapping("/scan")
    public ResponseEntity<BusPass> scanBusPass(@RequestParam String uid) {
        try {
            BusPass busPass = busPassService.scanBusPass(uid);
            return ResponseEntity.ok(busPass);
        } catch (RuntimeException e) {
            // You can customize error handling and return proper HTTP status
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * DRIVER: Scan bus pass QR code
     * Returns student info + bus pass details but no QR URL
     */
    @GetMapping("/driver/scan/{busPassUid}")
    public ResponseEntity<DriverBusPassDTO> driverScanBusPass(
            @PathVariable String busPassUid
    ) {
        BusPass busPass = busPassService.scanBusPass(busPassUid);

        DriverBusPassDTO dto = DriverBusPassDTO.builder()
                .rollNumber(busPass.getRollNumber())
                .admissionNumber(busPass.getAdmissionNumber())
                .studentName(busPass.getStudentName())
                .course(busPass.getCourse())
                .department(busPass.getDepartment())
                .busRoute(busPass.getBusRoute())
                .feePaid(busPass.getFeePaid())
                .issuedAt(busPass.getIssuedAt())
                .validTill(busPass.getValidTill())
                .status(busPass.getStatus().name())
                .studentPhotoUrl(busPass.getStudentPhotoUrl())
                .build();

        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/status/update")
    public ResponseEntity<?> updateStatusByRollNumber(
            @RequestParam String rollNumber,
            @RequestParam BusPassStatus status) {

        BusPass updated = busPassService.updateBusPassStatusByRollNumber(rollNumber, status);

        return ResponseEntity.ok(updated);
    }

}
