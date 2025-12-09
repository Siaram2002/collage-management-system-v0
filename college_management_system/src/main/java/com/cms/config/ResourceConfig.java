package com.cms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    // ================= STUDENT CONFIG =================
    @Value("${student.images.base-dir:uploads/photos/students}")
    private String studentImageBaseDir;

    @Value("${student.images.base-url:/photos/students}")
    private String studentImageBaseUrl;

    @Value("${student.qr.base-dir:uploads/qr/students}")
    private String studentQrBaseDir;

    @Value("${student.qr.base-url:/qr/students}")
    private String studentQrBaseUrl;

    // ================= DRIVER CONFIG =================
    @Value("${driver.images.base-dir:uploads/photos/drivers}")
    private String driverImageBaseDir;

    @Value("${driver.images.base-url:/photos/drivers}")
    private String driverImageBaseUrl;

    @Value("${driver.qr.base-dir:uploads/qr/drivers}")
    private String driverQrBaseDir;

    @Value("${driver.qr.base-url:/qr/drivers}")
    private String driverQrBaseUrl;

    // ================= ADMIN CONFIG =================
    @Value("${admin.images.base-dir:uploads/photos/admins}")
    private String adminImageBaseDir;

    @Value("${admin.images.base-url:/photos/admins}")
    private String adminImageBaseUrl;

    // ================= BUS PASS CONFIG =================
    @Value("${buspass.students.base-dir:buspass/students}")
    private String buspassStudentBaseDir;

    @Value("${buspass.students.base-url:/buspass/students}")
    private String buspassStudentBaseUrl;

    @Value("${buspass.qrImages.base-dir:buspass/qrImages}")
    private String buspassQrBaseDir;

    @Value("${buspass.qrImages.base-url:/buspass/qrImages}")
    private String buspassQrBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ---------------- STUDENT PATHS ----------------
        registry.addResourceHandler(studentImageBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(studentImageBaseDir).toAbsolutePath() + "/");

        registry.addResourceHandler(studentQrBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(studentQrBaseDir).toAbsolutePath() + "/");

        // ---------------- DRIVER PATHS ----------------
        registry.addResourceHandler(driverImageBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(driverImageBaseDir).toAbsolutePath() + "/");

        registry.addResourceHandler(driverQrBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(driverQrBaseDir).toAbsolutePath() + "/");

        // ---------------- ADMIN PATHS ----------------
        registry.addResourceHandler(adminImageBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(adminImageBaseDir).toAbsolutePath() + "/");

        // ---------------- BUS PASS STUDENT PATHS ----------------
        registry.addResourceHandler(buspassStudentBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(buspassStudentBaseDir).toAbsolutePath() + "/");

        // ---------------- BUS PASS QR PATHS ----------------
        registry.addResourceHandler(buspassQrBaseUrl + "/**")
                .addResourceLocations("file:" + Paths.get(buspassQrBaseDir).toAbsolutePath() + "/");
    }
}
