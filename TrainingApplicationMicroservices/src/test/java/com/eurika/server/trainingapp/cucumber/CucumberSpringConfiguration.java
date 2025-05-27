package com.eurika.server.trainingapp.cucumber;

import com.eurika.server.trainingapp.TrainingappApplication;
import com.eurika.server.trainingapp.dao.TraineeDao;
import com.eurika.server.trainingapp.dao.TrainerDao;
import com.eurika.server.trainingapp.dao.TrainingDao;
import com.eurika.server.trainingapp.dao.TrainingTypeDao;
import com.eurika.server.trainingapp.service.KafkaProducerService;
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