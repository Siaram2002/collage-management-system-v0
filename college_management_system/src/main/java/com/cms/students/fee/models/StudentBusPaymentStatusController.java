package com.cms.students.fee.models;

import com.cms.common.ApiResponse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bus-payment")
@RequiredArgsConstructor
public class StudentBusPaymentStatusController {

    private final StudentBusPaymentStatusService paymentService;

    @GetMapping("/{admissionNumber}")
    public ResponseEntity<ApiResponse> getStatus(@PathVariable String admissionNumber) {

        StudentBusPaymentStatus status = paymentService.getStatus(admissionNumber);

        if (status == null) {
            return ResponseEntity.ok(ApiResponse.fail("No payment record found"));
        }

        return ResponseEntity.ok(ApiResponse.success("Payment status fetched", status));
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse> updatePayment(@RequestBody PaymentUpdateRequest req) {

        if (req.getAdmissionNumber() == null || req.getAdmissionNumber().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Admission number is required"));
        }

        StudentBusPaymentStatus updated = paymentService.updateStatus(
                req.getAdmissionNumber(),
                req.getStatus(),
                req.getPaymentMode(),
                req.getTransactionId(),
                req.getRemarks()
        );

        return ResponseEntity.ok(ApiResponse.success("Payment status saved successfully", updated));
    }

    @GetMapping("/validate/{admissionNumber}")
    public ResponseEntity<ApiResponse> validatePayment(@PathVariable String admissionNumber) {

        boolean isPaid = paymentService.isPaymentValid(admissionNumber);

        if (!isPaid) {
            return ResponseEntity.ok(ApiResponse.fail("Bus fee not paid"));
        }

        return ResponseEntity.ok(ApiResponse.success("Bus fee is valid"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPayments() {

        List<StudentBusPaymentStatus> list = paymentService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Payment records fetched", list));
    }
}
