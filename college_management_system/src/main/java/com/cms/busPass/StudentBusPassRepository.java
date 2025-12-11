package com.cms.busPass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cms.students.models.Student;

import java.util.Optional;

@Repository
public interface StudentBusPassRepository extends JpaRepository<StudentBusPass, Long> {

    // Get the latest active bus pass UID for a student
    Optional<StudentBusPass> findByRollNumberAndAdmissionNumber(
            String rollNumber, String admissionNumber
    );

    // Fetch by busPassUid if needed
    Optional<StudentBusPass> findByBusPassUid(String busPassUid);

    // For checking existence
    boolean existsByRollNumberAndAdmissionNumber(String rollNumber, String admissionNumber);

	Optional<StudentBusPass> findByRollNumberOrAdmissionNumber(String rollNumber, String admissionNumber);

	Optional<StudentBusPass> findByRollNumber(String rollNumber);
}
