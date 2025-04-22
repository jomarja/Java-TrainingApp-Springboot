package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.config.JwtUtil;
import com.trainignapp.trainingapp.controller.AuthController;
import com.trainignapp.trainingapp.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TraineeService traineeService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private BruteForceProtectionService bruteForceService;
    @MockitoBean
    private RemoteTrainingServiceClient remoteTrainingServiceClient;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void login_ShouldReturn200IfAuthenticated() throws Exception {
        String username = "john.doe";
        String password = "password123";

        String jsonRequest = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        // 1) allow login
        Mockito.when(bruteForceService.isBlocked(username)).thenReturn(false);
        // 2) authenticate as trainer
        Mockito.when(trainerService.authenticateTrainer(username, password)).thenReturn(true);
        // 3) no trainee authentication
        Mockito.when(traineeService.authenticateTrainee(username, password)).thenReturn(false);

        // 4) stub UserDetails lookup
        UserDetails dummyPrincipal = Mockito.mock(UserDetails.class);
        Mockito.when(customUserDetailsService.loadUserByUsername(username))
                .thenReturn(dummyPrincipal);

        // 5) stub token generation
        Mockito.when(jwtUtil.generateToken(dummyPrincipal))
                .thenReturn("dummy-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"dummy-token\"}"));
    }


    @Test
    void login_ShouldReturnUnauthorizedIfInvalidCredentials() throws Exception {
        String username = "john.doe";
        String password = "wrongPassword";

        // Prepare the JSON login request.
        String jsonRequest = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        Mockito.when(trainerService.authenticateTrainer(username, password)).thenReturn(false);
        Mockito.when(traineeService.authenticateTrainee(username, password)).thenReturn(false);

        // Stub brute force protection as needed.
        Mockito.when(bruteForceService.isBlocked(username)).thenReturn(false);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid username or password"));
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
