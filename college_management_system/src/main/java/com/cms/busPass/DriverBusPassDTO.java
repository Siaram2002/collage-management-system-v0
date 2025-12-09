package com.cms.busPass;



import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DriverBusPassDTO {
    private String rollNumber;
    private String admissionNumber;
    private String studentName;
    private String course;
    private String department;
    private String busRoute;
    private BigDecimal feePaid;
    private LocalDateTime issuedAt;
    private LocalDateTime validTill;
    private String status;
    private String studentPhotoUrl;
}
