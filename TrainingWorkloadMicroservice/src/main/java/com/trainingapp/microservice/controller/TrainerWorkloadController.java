package com.trainingapp.microservice.controller;

import com.trainingapp.microservice.dto.TrainerWorkloadAggregateResponse;
import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.dto.TrainerWorkloadSummary;
import com.trainingapp.microservice.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workload")
public class TrainerWorkloadController {
    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadController.class);
    private final TrainerWorkloadService workloadService;

    @Autowired
    public TrainerWorkloadController(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request) {
        logger.info("Received update request: {}", request);
        workloadService.updateWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/{year}")
    public ResponseEntity<TrainerWorkloadSummary> getMonthlySummary(@PathVariable String username, @PathVariable int year) {
        TrainerWorkloadSummary summary = workloadService.getMonthlySummary(username, year);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/aggregate/{username}")
    public ResponseEntity<TrainerWorkloadAggregateResponse> getAggregateSummary(@PathVariable String username) {
        TrainerWorkloadAggregateResponse response = workloadService.getAggregateSummary(username);
        return ResponseEntity.ok(response);
    }
}
