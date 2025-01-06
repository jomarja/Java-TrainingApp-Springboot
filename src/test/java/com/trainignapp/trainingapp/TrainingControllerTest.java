package com.trainignapp.trainingapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainignapp.trainingapp.controller.TrainingController;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.dto.AddTrainingRequest;
import com.trainignapp.trainingapp.dto.TrainerDetails;
import com.trainignapp.trainingapp.dto.UpdateTraineeTrainersRequest;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.service.TrainingService;
import com.trainignapp.trainingapp.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainingService trainingService;
    @MockitoBean
    private TrainingTypeService trainingTypeService;
    @MockitoBean
    private TrainingTypeDao trainingTypeDao;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUnassignedActiveTrainers_ShouldReturnTrainers_WhenValidTraineeUsername() throws Exception {
        String traineeUsername = "trainee1";
        TrainingType fitnessType = new TrainingType();
        fitnessType.setId(1L);
        fitnessType.setTrainingTypeName("Fitness");

        TrainingType yogaType = new TrainingType();
        yogaType.setId(2L);
        yogaType.setTrainingTypeName("Yoga");

        Trainer trainer1 = new Trainer();
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setUsername("trainer1"); // Updated username to match the assertion
        trainer1.setPassword("password1");
        trainer1.setIsActive(true);
        trainer1.setSpecialization(fitnessType);

        Trainer trainer2 = new Trainer();
        trainer2.setFirstName("Jane");
        trainer2.setLastName("Smith");
        trainer2.setUsername("trainer2"); // Updated username to match the assertion
        trainer2.setPassword("password2");
        trainer2.setIsActive(true);
        trainer2.setSpecialization(yogaType);

        List<Trainer> trainers = List.of(trainer1, trainer2);
        Mockito.when(trainingService.getUnassignedTrainersForTrainee(traineeUsername)).thenReturn(trainers);

        mockMvc.perform(get("/api/trainings/{username}/unassigned-trainers", traineeUsername).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].username").value("trainer1")) // Updated
                .andExpect(jsonPath("$[0].specialization").value("Fitness")).andExpect(jsonPath("$[1].username").value("trainer2")) // Updated
                .andExpect(jsonPath("$[1].specialization").value("Yoga"));

        Mockito.verify(trainingService, Mockito.times(1)).getUnassignedTrainersForTrainee(traineeUsername);
    }

    @Test
    void updateTraineeTrainers_ShouldUpdateSuccessfully() throws Exception {
        String traineeUsername = "trainee1";

        // Prepare request payload
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerUsernames(List.of("trainer1", "trainer2"));

        // Mock service behavior with the mocked response
        List<TrainerDetails> mockedResponse = List.of(new TrainerDetails("trainer1", "John", "Doe", "Fitness"), new TrainerDetails("trainer2", "Jane", "Smith", "Yoga"));
        Mockito.when(trainingService.updateTraineeTrainers(Mockito.eq(traineeUsername), Mockito.anyList())).thenReturn(mockedResponse);

        // Perform PUT request with proper JSON payload
        mockMvc.perform(put("/api/trainings/{username}/trainers", traineeUsername).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk());

        // Verify service interaction
        Mockito.verify(trainingService, Mockito.times(1)).updateTraineeTrainers(Mockito.eq(traineeUsername), Mockito.anyList());
    }

    @Test
    void addTraining_ShouldReturnOk_WhenValidRequest() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("trainer1");
        request.setTrainingName("Fitness Training");
        request.setTrainingDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-12-01"));
        request.setTrainingDuration(60);
        request.setTrainingType("Fitness");

        mockMvc.perform(post("/api/trainings/add").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk());

        Mockito.verify(trainingService, Mockito.times(1)).addTraining(eq("trainee1"), eq("trainer1"), eq("Fitness Training"), Mockito.any(Date.class), eq(60), eq("Fitness"));
    }
}