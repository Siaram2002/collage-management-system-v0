package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cms")
@EnableJpaRepositories(basePackages = "com.cms")
@EntityScan(basePackages = "com.cms")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
