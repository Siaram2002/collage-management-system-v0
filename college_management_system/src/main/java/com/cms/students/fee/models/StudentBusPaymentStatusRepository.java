package com.cms.students.fee.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentBusPaymentStatusRepository extends JpaRepository<StudentBusPaymentStatus, Long> {

    Optional<StudentBusPaymentStatus> findByStudentAdmissionNumber(String admissionNumber);

    boolean existsByStudentAdmissionNumber(String admissionNumber);
}
