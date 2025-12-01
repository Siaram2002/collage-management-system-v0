package com.cms.students.config;



import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StudentImgConfig {

    @Value("${student.images.base-dir:uploads/photos/students}")
    private String studentImageBaseDir;

    @Value("${student.images.base-url:/photos/students}")
    private String studentImageBaseUrl;

    @Value("${student.images.extension:.png}")
    private String fileExtension;

    public String buildFileName(String uniqueId) {
        return uniqueId + fileExtension;
    }
}
