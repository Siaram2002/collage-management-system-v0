package com.cms.students.config;



import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StudentQrImageConfig {

    @Value("${student.qr.base-dir:uploads/qr/students}")
    private String studentQrBaseDir;

    @Value("${student.qr.base-url:/qr/students}")
    private String studentQrBaseUrl;

    @Value("${student.qr.extension:.png}")
    private String qrFileExtension;

    public String buildQrFileName(String uniqueId) {
        return uniqueId + qrFileExtension;
    }
}
