package com.cms.transport.service;

import com.cms.transport.bus.models.Bus;
import com.cms.transport.dto.BusGpsDto;
import com.cms.transport.dto.TransportAssignmentDTO;
import com.cms.transport.models.TransportAssignment;

import java.util.List;

public interface TransportAssignmentService {

    TransportAssignment createAssignment(TransportAssignmentDTO dto);

    TransportAssignment updateAssignment(Long assignmentId, TransportAssignmentDTO dto);

    TransportAssignment getAssignment(Long assignmentId);

    List<TransportAssignment> getAllAssignments();

    void deleteAssignment(Long assignmentId);
    
    List<BusGpsDto> getBusesByRoute(Long routeId);

}
