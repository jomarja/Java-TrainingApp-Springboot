package com.trainignapp.trainingapp.controller;

import com.trainignapp.trainingapp.dto.*;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Api(value = "Trainee Management System")
public class TraineeController {
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @Autowired
    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @ApiOperation(value = "Register a new trainee", response = TraineeRegistrationResponse.class)
    @PostMapping("/register")
    public ResponseEntity<TraineeRegistrationResponse> registerTrainee(@Validated @RequestBody TraineeRegistrationRequest request) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        traineeService.createTrainee(trainee);

        TraineeRegistrationResponse response = new TraineeRegistrationResponse(trainee.getUsername(), trainee.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation(value = "Get Trainee Profile", notes = "Fetch the profile of a trainee along with their associated trainers.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved trainee profile"), @ApiResponse(code = 404, message = "Trainee not found")})

    @GetMapping("/{username}/profile")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@ApiParam(value = "Username of the trainee to fetch", required = true) @PathVariable String username) {
        TraineeProfileResponse profileResponse = traineeService.getTraineeProfile(username);
        return ResponseEntity.ok(profileResponse);
    }

    @ApiOperation(value = "Update Trainee Profile", response = TraineeProfileResponse.class)
    @PutMapping("/{username}/update")
    public ResponseEntity<TraineeUpdatedProfileResponse> updateTraineeProfile(@ApiParam(value = "Username of the trainee to update", required = true) @PathVariable String username, @Validated @RequestBody UpdateTraineeProfileRequest request) {

        TraineeUpdatedProfileResponse response = traineeService.updateProfile(username, request);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Delete a trainee profile", response = String.class)
    @DeleteMapping("/{username}/delete")
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.ok("Trainee profile deleted successfully");
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation(value = "Get Trainee's Trainings List", response = List.class)
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(@PathVariable String username, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to, @RequestParam(required = false) String trainerName, @RequestParam(required = false) String trainingType) {

        List<TraineeTrainingResponse> trainings = trainingService.getTraineeTrainingsByCriteria(username, from, to, trainerName, trainingType);
        return ResponseEntity.ok(trainings);
    }

    @ApiOperation(value = "Activate/De-Activate a trainee", response = ResponseEntity.class)
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTraineeActivation(@PathVariable String username, @RequestParam boolean isActive) {
        traineeService.deactivateTrainee(username, isActive);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}