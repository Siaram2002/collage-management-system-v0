package com.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/uploads/**")
//                .addResourceLocations("file:uploads/");
//    }
//@Override
//public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    registry.addResourceHandler("/uploads/faculty/**")
//            .addResourceLocations("file:uploads/faculty/");
//}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String facultyUploadPath = Paths.get("uploads/faculty/").toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/uploads/faculty/**")
                .addResourceLocations("file:" + facultyUploadPath);
    }


}

