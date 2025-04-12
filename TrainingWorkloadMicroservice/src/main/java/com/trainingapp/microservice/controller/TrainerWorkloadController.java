package com.trainingapp.microservice.controller;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.dto.TrainerWorkloadSummary;
import com.trainingapp.microservice.mongo.TrainerTrainingSummary;
import com.trainingapp.microservice.repository.TrainerTrainingSummaryRepository;
import com.trainingapp.microservice.service.TrainerMongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workload")
public class TrainerWorkloadController {
    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadController.class);
    private final TrainerMongoService trainerMongoService;
    private final TrainerTrainingSummaryRepository trainerTrainingSummaryRepository;

    @Autowired
    public TrainerWorkloadController(TrainerMongoService trainerMongoService, TrainerTrainingSummaryRepository trainerTrainingSummaryRepository) {
        this.trainerMongoService = trainerMongoService;
        this.trainerTrainingSummaryRepository = trainerTrainingSummaryRepository;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTrainerSummary(@RequestBody TrainerWorkloadRequest request) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction {}: Received update request for trainer: {}", transactionId, request.getTrainerUsername());
        trainerMongoService.updateTrainerSummary(request, transactionId);
        return ResponseEntity.ok("Update processed for trainer: " + request.getTrainerUsername() + " - TransactionID: " + transactionId);
    }

    @GetMapping("/{username}/{year}")
    public ResponseEntity<TrainerWorkloadSummary> getMonthlySummary(@PathVariable String username, @PathVariable int year) {
        TrainerWorkloadSummary summary = trainerMongoService.getMonthlySummary(username, year);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("aggregate/{username}")
    public ResponseEntity<TrainerTrainingSummary> getTrainerSummary(@PathVariable String username) {
        TrainerTrainingSummary summary = trainerTrainingSummaryRepository.findByTrainerUsername(username);
        if (summary == null) {
            logger.warn("No training summary found for trainer: {}", username);
            return ResponseEntity.notFound().build();
        }
        logger.info("Found training summary for trainer: {}", username);
        return ResponseEntity.ok(summary);
    }
}
