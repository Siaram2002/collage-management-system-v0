package com.cms.students.fee.models;

import com.cms.common.exceptions.ResourceNotFoundException;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentBusPaymentStatusServiceImpl implements StudentBusPaymentStatusService {

    private final StudentBusPaymentStatusRepository repository;

    @Override
    public StudentBusPaymentStatus getStatus(String admissionNumber) {

        if (admissionNumber == null || admissionNumber.isBlank()) {
            throw new IllegalArgumentException("Admission number must not be empty");
        }

        return repository.findByStudentAdmissionNumber(admissionNumber).orElse(null);
    }

    @Override
    public StudentBusPaymentStatus updateStatus(
            String admissionNumber,
            PaymentStatus status,
            PaymentMode paymentMode,
            String transactionId,
            String remarks) {

        if (admissionNumber == null || admissionNumber.isBlank()) {
            throw new IllegalArgumentException("Admission number is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Payment status is required");
        }

        try {

            StudentBusPaymentStatus existing =
                    repository.findByStudentAdmissionNumber(admissionNumber).orElse(null);

            StudentBusPaymentStatus entity =
                    existing != null ? existing : new StudentBusPaymentStatus();

            entity.setStudentAdmissionNumber(admissionNumber);
            entity.setStatus(status);
            entity.setPaymentMode(paymentMode); // FIXED
            entity.setTransactionId(transactionId);
            entity.setRemarks(remarks);

            StudentBusPaymentStatus saved = repository.save(entity);

            log.info("Saved payment record: admission={}, status={}", admissionNumber, status);

            return saved;

        } catch (Exception e) {
            log.error("Error saving payment record for {}: {}", admissionNumber, e.getMessage(), e);
            throw new RuntimeException("Unable to save payment status");
        }
    }

    @Override
    public void delete(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Payment record not found: " + id);
        }

        repository.deleteById(id);
        log.info("Deleted payment record ID={}", id);
    }

    @Override
    public boolean isPaymentValid(String admissionNumber) {

        if (admissionNumber == null || admissionNumber.isBlank()) {
            throw new IllegalArgumentException("Admission number is required");
        }

        StudentBusPaymentStatus status =
                repository.findByStudentAdmissionNumber(admissionNumber).orElse(null);

        if (status == null) {
            return false;
        }

        return status.getStatus() == PaymentStatus.PAID;
    }

    @Override
    public List<StudentBusPaymentStatus> getAll() {
        return repository.findAll();
    }
}
