package com.trainingapp.microservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue     = {
                "com.trainingapp.microservice.cucumber",
                "com.trainingapp.microservice.cucumber.steps"
        },
        plugin   = {"pretty"}
)
public class RunCucumberTest { }