package com.cms.transport.dto;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDTO {

    private LocalDate assignmentDate;

    private String busNumber;

    private String driverName;

    private String driverPhoto;

    private String routeName;

    private String shift;

    private LocalDate startDate;

    private LocalDate endDate;

    // ✅ NEW FIELD
    private String leaveStatus;   // AVAILABLE / ON_LEAVE
}

