package com.cms.students.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${student.images.base-dir:uploads/photos/students}")
    private String studentImageBaseDir;

    @Value("${student.images.base-url:/photos/students}")
    private String studentImageBaseUrl;

    @Value("${student.qr.base-dir:uploads/qr/students}")
    private String studentQrBaseDir;

    @Value("${student.qr.base-url:/qr/students}")
    private String studentQrBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Convert relative paths → absolute paths
        String imageAbsolutePath = Paths.get(studentImageBaseDir).toAbsolutePath().toString() + "/";
        String qrAbsolutePath = Paths.get(studentQrBaseDir).toAbsolutePath().toString() + "/";

        registry
            .addResourceHandler(studentImageBaseUrl + "/**")
            .addResourceLocations("file:" + imageAbsolutePath);

        registry
            .addResourceHandler(studentQrBaseUrl + "/**")
            .addResourceLocations("file:" + qrAbsolutePath);
    }
}
