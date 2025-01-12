package com.trainignapp.trainingapp.controller;

import com.trainignapp.trainingapp.dto.*;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.service.TrainingService;
import com.trainignapp.trainingapp.service.TrainingTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@Api(value = "Training Management System")
public class TrainingController {
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingController(TrainingService trainingService, TrainingTypeService trainingTypeService) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    @ApiOperation(value = "Get active trainers not assigned to a specific trainee", response = TrainerTraineeResponse.class, responseContainer = "List")
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerDetails>> getUnassignedActiveTrainers(@PathVariable String username) {
        // Use the TrainingService method
        List<Trainer> unassignedTrainers = trainingService.getUnassignedTrainersForTrainee(username);

        // Map to DTO for response
        List<TrainerDetails> response = unassignedTrainers.stream().filter(Trainer::getIsActive) // Ensure trainers are active
                .map(trainer -> new TrainerDetails(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization().getTrainingTypeName())).toList();

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Update a trainee's trainer list")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainer list updated successfully"), @ApiResponse(code = 404, message = "Trainee not found"), @ApiResponse(code = 400, message = "Invalid input")})
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerDetails>> updateTraineeTrainers(@PathVariable String username, @RequestBody UpdateTraineeTrainersRequest request) {
        List<TrainerDetails> updatedTrainers = trainingService.updateTraineeTrainers(username, request.getTrainerUsernames());
        return ResponseEntity.ok(updatedTrainers);
    }

    @ApiOperation(value = "Add a new training", response = ResponseEntity.class)
    @PostMapping("/add")
    public ResponseEntity<Void> addTraining(@Validated @RequestBody AddTrainingRequest request) {
        trainingService.addTraining(request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration(), request.getTrainingType());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "Get all training types", response = List.class)
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        List<TrainingTypeResponse> trainingTypes = trainingTypeService.getAllTrainingTypes();
        return ResponseEntity.ok(trainingTypes);
    }
}