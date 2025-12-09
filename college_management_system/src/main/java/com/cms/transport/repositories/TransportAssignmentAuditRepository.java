package com.cms.transport.repositories;



import com.cms.transport.models.TransportAssignmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportAssignmentAuditRepository extends JpaRepository<TransportAssignmentAudit, Long> {

    /**
     * Fetch all audit records for a specific assignment, ordered by change time descending
     */
    List<TransportAssignmentAudit> findByAssignmentIdOrderByChangedAtDesc(Long assignmentId);

}
