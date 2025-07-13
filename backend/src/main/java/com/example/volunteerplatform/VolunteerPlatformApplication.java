package com.example.volunteerplatform;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.volunteerplatform", "com.example.service", "com.example.util", "com.example.config"})
@MapperScan("com.example.mapper")
public class VolunteerPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolunteerPlatformApplication.class, args);
    }

}