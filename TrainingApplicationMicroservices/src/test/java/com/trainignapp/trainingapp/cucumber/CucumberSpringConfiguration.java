package com.trainignapp.trainingapp.cucumber;

import com.trainignapp.trainingapp.TrainingappApplication;
import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.service.KafkaProducerService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainingappApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CucumberSpringConfiguration {
    @MockitoBean
    TraineeDao traineeDao;
    @MockitoBean
    TrainerDao trainerDao;
    @MockitoBean
    TrainingDao trainingDao;
    @MockitoBean
    TrainingTypeDao trainingTypeDao;
    @MockitoBean
    KafkaProducerService kafkaProducerService;
}