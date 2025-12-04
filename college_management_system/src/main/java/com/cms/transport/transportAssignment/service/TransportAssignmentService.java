package com.cms.transport.transportAssignment.service;

import com.cms.transport.transportAssignment.dto.TransportAssignmentRequest;
import com.cms.transport.transportAssignment.dto.TransportAssignmentResponse;

import java.util.List;

public interface TransportAssignmentService {

    TransportAssignmentResponse createAssignment(TransportAssignmentRequest request);

    TransportAssignmentResponse getAssignmentById(Long id);

    List<TransportAssignmentResponse> getAllAssignments();

    TransportAssignmentResponse updateAssignment(Long id, TransportAssignmentRequest request);

    void deleteAssignment(Long id);



}
