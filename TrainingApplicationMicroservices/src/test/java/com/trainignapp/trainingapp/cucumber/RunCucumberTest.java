package com.trainignapp.trainingapp.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", glue = "com.trainignapp.trainingapp.cucumber", plugin = "pretty")
public class RunCucumberTest {
}