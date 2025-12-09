package com.cms.students.fee.models;



import lombok.Data;

@Data
public class PaymentUpdateRequest {

    private String admissionNumber;
    private PaymentStatus status;
    private PaymentMode paymentMode;
    private String transactionId;
    private String remarks;
}
