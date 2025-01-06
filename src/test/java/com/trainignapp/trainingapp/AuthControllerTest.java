package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.controller.AuthController;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TraineeService traineeService;

    @Test
    void login_ShouldReturn200IfAuthenticated() throws Exception {
        String username = "john.doe";
        String password = "password123";

        Mockito.when(trainerService.authenticateTrainer(username, password)).thenReturn(true);

        mockMvc.perform(get("/api/auth/login").param("username", username).param("password", password)).andExpect(status().isOk()).andExpect(content().string("Login successful"));
    }

    @Test
    void login_ShouldReturn400IfInvalidCredentials() throws Exception {
        String username = "john.doe";
        String password = "wrongPassword";

        Mockito.when(trainerService.authenticateTrainer(username, password)).thenReturn(false);
        Mockito.when(traineeService.authenticateTrainee(username, password)).thenReturn(false);

        mockMvc.perform(get("/api/auth/login").param("username", username).param("password", password)).andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_ShouldReturn200IfPasswordUpdated() throws Exception {
        String jsonRequest = """
                {
                    "username": "john.doe",
                    "oldPassword": "oldPass123",
                    "newPassword": "newPass123"
                }
                """;

        Mockito.when(trainerService.authenticateTrainer("john.doe", "oldPass123")).thenReturn(true);

        mockMvc.perform(put("/api/auth/change-password").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isOk()).andExpect(content().string("Password changed successfully"));
    }

    @Test
    void changePassword_ShouldReturn400IfAuthenticationFails() throws Exception {
        String jsonRequest = """
                {
                    "username": "john.doe",
                    "oldPassword": "wrongPass",
                    "newPassword": "newPass123"
                }
                """;

        Mockito.when(trainerService.authenticateTrainer("john.doe", "wrongPass")).thenReturn(false);
        Mockito.when(traineeService.authenticateTrainee("john.doe", "wrongPass")).thenReturn(false);

        mockMvc.perform(put("/api/auth/change-password").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isBadRequest());
    }
}
