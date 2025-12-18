package com.cms.busPass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ADMIN: View model for showing student + current bus pass summary
 * on the "Generate Bus Pass" screen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBusPassStudentDTO {

    // Basic student info
    private Long studentId;
    private String fullName;
    private String rollNumber;
    private String admissionNumber;

    private String departmentCode;
    private String courseCode;

    private Boolean transportEnabled;
    private String studentPhotoUrl;

    // Current / latest bus pass summary (if any)
    private String currentBusPassUid;
    private String currentStatus;
    private LocalDateTime currentValidTill;
    private String currentBusRoute;
    private String currentRouteCode;
    private String currentQrUrl;
}


