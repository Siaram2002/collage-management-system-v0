package com.cms.busPass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ADMIN: Simple JSON payload to issue/renew a bus pass for a student
 * without requiring a photo upload (we will reuse existing student photo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBusPassIssueRequest {

    // Student identifiers (either one is sufficient)
    private String rollNumber;
    private String admissionNumber;

    // Bus pass info
    private String busRoute;
    private String feePaid;           // will be converted to BigDecimal
    private Integer validityInMonths; // validity in months
    private String routeCode;
}


