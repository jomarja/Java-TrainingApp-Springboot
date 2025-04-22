package com.trainingapp.microservice.cucumber;


import com.trainingapp.microservice.MicroserviceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        classes = MicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class CucumberSpringConfiguration {
    // empty—just hooks Cucumber into Spring Boot
}