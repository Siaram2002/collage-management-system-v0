package com.cms.students.fee.models;




import com.cms.students.fee.models.StudentBusPaymentStatus;

import java.util.List;
import java.util.Optional;

public interface StudentBusPaymentStatusService {

    StudentBusPaymentStatus getStatus(String admissionNumber);

    StudentBusPaymentStatus updateStatus(
            String admissionNumber,
            PaymentStatus status,
            PaymentMode paymentMode,
            String transactionId,
            String remarks
    );

    boolean isPaymentValid(String admissionNumber);

	List<StudentBusPaymentStatus> getAll();

	void delete(Long id);


}
