package com.trainignapp.trainingapp.controller;

import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.dto.*;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.service.TrainerService;
import com.trainignapp.trainingapp.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@Api(value = "Trainer Management System")
public class TrainerController {
    private final TrainerService trainerService;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingService trainingService;

    @Autowired
    public TrainerController(TrainerService trainerService, TrainingTypeDao trainingTypeDao, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingTypeDao = trainingTypeDao;
        this.trainingService = trainingService;
    }

    @ApiOperation(value = "Register a new trainer", response = TrainerRegistrationResponse.class)
    @PostMapping("/register")
    public ResponseEntity<TrainerRegistrationResponse> registerTrainer(@Validated @RequestBody TrainerRegistrationRequest request) {

        TrainingType specialization = trainingTypeDao.findByName(request.getSpecialization()).orElseThrow(() -> new IllegalArgumentException("Invalid specialization: " + request.getSpecialization()));
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setSpecialization(specialization);

        trainerService.createTrainer(trainer);

        TrainerRegistrationResponse response = new TrainerRegistrationResponse(trainer.getUsername(), trainer.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation(value = "Get Trainer Profile", response = TrainerProfileResponse.class)
    @GetMapping("/{username}/profile")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable String username) {
        TrainerProfileResponse profileResponse = trainerService.getTrainerProfile(username);
        return ResponseEntity.ok(profileResponse);
    }

    @ApiOperation(value = "Update trainer profile", response = TrainerProfileResponse.class)
    @PutMapping("/{username}/update")
    public ResponseEntity<TrainerProfileResponseFull> updateTrainerProfile(@PathVariable String username, @Validated @RequestBody UpdateTrainerProfileRequest request) {
        TrainerProfileResponseFull response = trainerService.updateProfile(username, request);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Get Trainer's Trainings List", response = List.class)
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(@PathVariable String username, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to, @RequestParam(required = false) String traineeName) {

        List<TrainerTrainingResponse> trainings = trainingService.getTrainerTrainingsByCriteria(username, from, to, traineeName);

        return ResponseEntity.ok(trainings);
    }

    @ApiOperation(value = "Activate/De-Activate a trainer", response = ResponseEntity.class)
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTrainerActivation(@PathVariable String username, @RequestParam boolean isActive) {
        trainerService.deactivateTrainer(username, isActive);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
