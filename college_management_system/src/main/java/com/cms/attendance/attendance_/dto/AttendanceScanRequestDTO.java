package com.cms.attendance.attendance_.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceScanRequestDTO {

    private String qrPayload;
    private Long studentId;
}
