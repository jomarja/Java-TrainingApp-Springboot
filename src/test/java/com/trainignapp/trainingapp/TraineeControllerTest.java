package com.trainignapp.trainingapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainignapp.trainingapp.controller.TraineeController;
import com.trainignapp.trainingapp.dto.*;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainingService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {
    @MockitoBean
    public TraineeService traineeService;
    @MockitoBean
    public TrainingService trainingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerTrainee_ShouldReturnCreatedStatus() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        String jsonRequest = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        Mockito.doNothing().when(traineeService).createTrainee(Mockito.any());

        mockMvc.perform(post("/api/trainees/register").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isCreated());
    }

    @Test
    void getTraineeProfile_ShouldReturnTraineeProfile() throws Exception {
        String username = "john.doe";

        // Mock data
        TraineeTrainerResponse trainerResponse = new TraineeTrainerResponse("trainer1", "Jane", "Smith", "Fitness");

        TraineeProfileResponse mockResponse = new TraineeProfileResponse("John", "Doe", new Date(), "123 Main Street", true, Collections.singletonList(trainerResponse));

        Mockito.when(traineeService.getTraineeProfile(username)).thenReturn(mockResponse);

        // Perform GET request
        mockMvc.perform(get("/api/trainees/{username}/profile", username).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.lastName").value("Doe")).andExpect(jsonPath("$.address").value("123 Main Street")).andExpect(jsonPath("$.isActive").value(true)).andExpect(jsonPath("$.trainers[0].username").value("trainer1")).andExpect(jsonPath("$.trainers[0].firstName").value("Jane")).andExpect(jsonPath("$.trainers[0].specialization").value("Fitness"));

        // Verify the service method was called
        Mockito.verify(traineeService, Mockito.times(1)).getTraineeProfile(username);
    }

    @Test
    void getTraineeProfile_ShouldReturnNotFound_WhenTraineeDoesNotExist() throws Exception {
        String username = "unknown";

        Mockito.when(traineeService.getTraineeProfile(username)).thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(get("/api/trainees/{username}/profile", username).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Trainee not found"));

        Mockito.verify(traineeService, Mockito.times(1)).getTraineeProfile(username);
    }

    @Test
    void updateTraineeProfile_ShouldReturnUpdatedProfile() throws Exception {
        String username = "john.doe";
        UpdateTraineeProfileRequest request = new UpdateTraineeProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(new Date());
        request.setAddress("123 Main Street");
        request.setIsActive(true);

        TraineeTrainerResponse trainerResponse = new TraineeTrainerResponse("trainer1", "Jane", "Smith", "Fitness");

        TraineeUpdatedProfileResponse response = new TraineeUpdatedProfileResponse(username, "John", "Doe", new Date(), "123 Main Street", true, Collections.singletonList(trainerResponse));

        Mockito.when(traineeService.updateProfile(eq(username), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/trainees/{username}/update", username).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.trainers[0].username").value("trainer1"));

        Mockito.verify(traineeService, Mockito.times(1)).updateProfile(eq(username), Mockito.any());
    }

    @Test
    void updateTraineeProfile_ShouldReturnNotFound_WhenTraineeDoesNotExist() throws Exception {
        String username = "unknown";

        UpdateTraineeProfileRequest request = new UpdateTraineeProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("123 Main St");
        request.setIsActive(true);
        request.setDateOfBirth(new Date());

        Mockito.when(traineeService.updateProfile(eq(username), Mockito.any())).thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(put("/api/trainees/{username}/update", username).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Trainee not found"));

        Mockito.verify(traineeService, Mockito.times(1)).updateProfile(eq(username), Mockito.any());
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOk_WhenTraineeExists() throws Exception {
        String username = "John.Doe";

        Mockito.doNothing().when(traineeService).deleteTrainee(username);

        mockMvc.perform(delete("/api/trainees/{username}/delete", username)).andExpect(status().isOk()).andExpect(content().string("Trainee profile deleted successfully"));

        Mockito.verify(traineeService, Mockito.times(1)).deleteTrainee(username);
    }

    @Test
    void deleteTraineeProfile_ShouldReturnNotFound_WhenTraineeDoesNotExist() throws Exception {
        String username = "unknown";

        Mockito.doThrow(new EntityNotFoundException("Trainee not found")).when(traineeService).deleteTrainee(username);

        mockMvc.perform(delete("/api/trainees/{username}/delete", username)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Trainee not found"));

        Mockito.verify(traineeService, Mockito.times(1)).deleteTrainee(username);
    }

    @Test
    void toggleTraineeActivation_ShouldReturnOk_WhenSuccessful() throws Exception {
        String username = "john.doe";
        boolean isActive = false;

        Mockito.doNothing().when(traineeService).deactivateTrainee(username, isActive);

        mockMvc.perform(patch("/api/trainees/{username}/activate", username).param("isActive", String.valueOf(isActive)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        Mockito.verify(traineeService, Mockito.times(1)).deactivateTrainee(username, isActive);
    }
}