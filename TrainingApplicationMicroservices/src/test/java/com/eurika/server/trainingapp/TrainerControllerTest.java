package com.eurika.server.trainingapp;

import com.eurika.server.trainingapp.config.JwtUtil;
import com.eurika.server.trainingapp.controller.TrainerController;
import com.eurika.server.trainingapp.dao.TrainingTypeDao;
import com.eurika.server.trainingapp.dto.*;
import com.eurika.server.trainingapp.model.TrainingType;
import com.eurika.server.trainingapp.service.TrainerService;
import com.eurika.server.trainingapp.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TrainingTypeDao trainingTypeDao;
    @MockitoBean
    private TrainingService trainingService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void registerTrainer_ShouldReturnCreatedStatus() throws Exception {
        // Arrange
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialization("Boxing");

        TrainingType boxing = new TrainingType();
        boxing.setId(1L);
        boxing.setTrainingTypeName("Boxing");

        // Mock the behavior of trainingTypeDao
        Mockito.when(trainingTypeDao.findByName("Boxing")).thenReturn(Optional.of(boxing));

        // Mock the behavior of trainerService
        Mockito.doNothing().when(trainerService).createTrainer(Mockito.any());

        // Act & Assert
        mockMvc.perform(post("/api/trainers/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    void getTrainerProfile_ShouldReturnOk_WhenTrainerExists() throws Exception {
        String username = "john.doe";

        TrainerProfileResponse response = new TrainerProfileResponse("John", "Doe", "Fitness", true, List.of(new TrainerTraineeResponse("jane.doe", "Jane", "Doe")));

        Mockito.when(trainerService.getTrainerProfile(username)).thenReturn(response);

        mockMvc.perform(get("/api/trainers/{username}/profile", username).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.specialization").value("Fitness")).andExpect(jsonPath("$.trainees[0].username").value("jane.doe"));
    }

    @Test
    void updateTrainerProfile_ShouldReturnOk_WhenValidRequest() throws Exception {
        String username = "john.doe";

        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);

        TrainerProfileResponseFull response = new TrainerProfileResponseFull(username, "John", "Doe", "Fitness", true, List.of(new TrainerTraineeResponse("jane.doe", "Jane", "Doe")));

        Mockito.when(trainerService.updateProfile(eq(username), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/trainers/{username}/update", username).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.username").value(username)).andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void toggleTrainerActivation_ShouldReturnOk_WhenSuccessful() throws Exception {
        String username = "jane.doe";
        boolean isActive = true;

        Mockito.doNothing().when(trainerService).deactivateTrainer(username, isActive);

        mockMvc.perform(patch("/api/trainers/{username}/activate", username).param("isActive", String.valueOf(isActive)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        Mockito.verify(trainerService, Mockito.times(1)).deactivateTrainer(username, isActive);
    }
}