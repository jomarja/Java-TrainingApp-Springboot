package com.trainignapp.trainingapp.controller;

import com.trainignapp.trainingapp.dto.ChangePasswordRequest;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Api(value = "Authentication Management System")
public class AuthController {
    private final TrainerService trainerService;
    private final TraineeService traineeService;

    @Autowired
    public AuthController(TrainerService trainerService, TraineeService traineeService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
    }

    @ApiOperation(value = "User Login", notes = "Authenticate user by username and password.")
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        boolean isAuthenticated = trainerService.authenticateTrainer(username, password) || traineeService.authenticateTrainee(username, password);

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
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
}
