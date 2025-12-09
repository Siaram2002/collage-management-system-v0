package com.cms.busPass;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusPassRequest {

    // Student identifiers
    private String rollNumber;
    private String admissionNumber;

    // Student details (for bus pass record)
    private String studentName;
    private String courseName;
    private String departmentName;

    // Bus pass info
    private String busRoute;
    private String feePaid;           // as string, will convert to BigDecimal
    private Integer validityInMonths; // Number of months for validity
    private String routeCode;

    // Student photo (optional, required for issuing)
    private MultipartFile studentPhoto;
}
