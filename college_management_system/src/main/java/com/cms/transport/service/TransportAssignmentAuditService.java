package com.cms.transport.service;

import com.cms.transport.models.TransportAssignment;
import com.cms.transport.models.TransportAssignmentAudit;
import com.cms.transport.repositories.TransportAssignmentAuditRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportAssignmentAuditService {

    private final TransportAssignmentAuditRepository auditRepo;

    /**
     * Save a new audit record for an assignment
     */
    public void saveAudit(TransportAssignment oldAssignment, TransportAssignment newAssignment) {
        TransportAssignmentAudit audit = TransportAssignmentAudit.builder()
                .assignmentId(oldAssignment != null ? oldAssignment.getAssignmentId() : (newAssignment != null ? newAssignment.getAssignmentId() : null))
                
                // Old values
                .oldBus(oldAssignment != null ? oldAssignment.getBus() : null)
                .oldDriver(oldAssignment != null ? oldAssignment.getDriver() : null)
                .oldRoute(oldAssignment != null ? oldAssignment.getRoute() : null)
                .oldAssignmentDate(oldAssignment != null ? oldAssignment.getAssignmentDate() : null)
                .oldStatus(oldAssignment != null ? oldAssignment.getStatus() : null)
                
                // New values
                .newBus(newAssignment != null ? newAssignment.getBus() : null)
                .newDriver(newAssignment != null ? newAssignment.getDriver() : null)
                .newRoute(newAssignment != null ? newAssignment.getRoute() : null)
                .newAssignmentDate(newAssignment != null ? newAssignment.getAssignmentDate() : null)
                .newStatus(newAssignment != null ? newAssignment.getStatus() : null)
                
                .build();

        auditRepo.save(audit);
    }
    
    
    

    /**
     * Get full audit history for a given assignment
     */
    public List<TransportAssignmentAudit> getAuditHistory() {
        return auditRepo.findAll();
    }
}
