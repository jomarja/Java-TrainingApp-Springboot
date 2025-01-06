package com.trainignapp.trainingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.trainignapp.trainingapp")
public class TrainingappApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingappApplication.class, args);
    }
}
