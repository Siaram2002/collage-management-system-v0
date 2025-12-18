package com.cms.transport.driver.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRScanLogTableDTO {

    private String photo;        // Student photo URL
    private String studentName;  // Rahul Kumar
    private String rollNo;       // CS2023-029
    private String busRoute;     // Route A
    private String scanTime;     // HH:mm
    private String location;     // Main Gate, Dindigul
    private String status;       // SUCCESS / FAILED
}
