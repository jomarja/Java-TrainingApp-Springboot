package com.trainignapp.trainingapp.controller;

import com.trainignapp.trainingapp.config.JwtUtil;
import com.trainignapp.trainingapp.dto.ChangePasswordRequest;
import com.trainignapp.trainingapp.dto.LoginRequest;
import com.trainignapp.trainingapp.service.BruteForceProtectionService;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(value = "Authentication Management System")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtUtil jwtUtil;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final BruteForceProtectionService bruteForceService;

    @Autowired
    public AuthController(TrainerService trainerService, TraineeService traineeService, JwtUtil jwtUtil, BruteForceProtectionService bruteForceService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.jwtUtil = jwtUtil;
        this.bruteForceService = bruteForceService;
    }

    @ApiOperation(value = "User Login", notes = "Authenticate user by username and password and generate JWT.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        String username = request.getUsername();

        if (bruteForceService.isBlocked(username)) {
            logger.warn("User {} is blocked due to multiple failed login attempts.", username);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many failed login attempts. Try again in 5 minutes.");
        }

        boolean isAuthenticated = traineeService.authenticateTrainee(request.getUsername(), request.getPassword()) || trainerService.authenticateTrainer(request.getUsername(), request.getPassword());

        if (!isAuthenticated) {
            logger.warn("Authentication failed for user: {}", request.getUsername());
            bruteForceService.recordFailedLogin(username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        bruteForceService.resetFailedAttempts(username);

        String token = jwtUtil.generateToken(request.getUsername());

        logger.info("User {} logged in successfully. Token issued.", request.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @ApiOperation(value = "Change Login Password", notes = "Update user password after verifying the old password.")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Validated @RequestBody ChangePasswordRequest request) {
        boolean isTrainer = trainerService.authenticateTrainer(request.getUsername(), request.getOldPassword());
        boolean isTrainee = traineeService.authenticateTrainee(request.getUsername(), request.getOldPassword());

        if (isTrainer) {
            trainerService.updateTrainerPassword(request.getUsername(), request.getNewPassword());
        } else if (isTrainee) {
            traineeService.updateTraineePassword(request.getUsername(), request.getNewPassword());
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return ResponseEntity.ok("Password changed successfully");
    }

    @ApiOperation(value = "User Logout", notes = "Clear authentication session")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid token found");
        }

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
}