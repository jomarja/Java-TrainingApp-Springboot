package com.trainingapp.microservice;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.mongo.TrainerTrainingSummary;
import com.trainingapp.microservice.repository.TrainerTrainingSummaryRepository;
import com.trainingapp.microservice.service.TrainerMongoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerMongoServiceTest {
    @Mock
    private TrainerTrainingSummaryRepository repository;
    @InjectMocks
    private TrainerMongoService trainerMongoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewTrainerSummary() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("trainer1");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(30);
        request.setActionType("ADD");

        when(repository.findByTrainerUsername("trainer1")).thenReturn(null);

        String transactionId = "tx123";
        trainerMongoService.updateTrainerSummary(request, transactionId);

        verify(repository, times(1)).save(any(TrainerTrainingSummary.class));
    }
}
