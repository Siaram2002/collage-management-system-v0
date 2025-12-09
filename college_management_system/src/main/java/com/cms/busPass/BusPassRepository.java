package com.cms.busPass;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusPassRepository extends JpaRepository<BusPass, Long> {

    // Fetch by unique UID (QR scanned)
    Optional<BusPass> findByBusPassUid(String busPassUid);

    // Fetch latest bus pass for a student (for renewal or viewing)
    Optional<BusPass> findTopByRollNumberAndAdmissionNumberOrderByIssuedAtDesc(
            String rollNumber, String admissionNumber
    );

    // Check if student currently has an ACTIVE pass
    boolean existsByRollNumberAndAdmissionNumberAndStatus(
            String rollNumber, String admissionNumber, BusPassStatus status
    );
}
