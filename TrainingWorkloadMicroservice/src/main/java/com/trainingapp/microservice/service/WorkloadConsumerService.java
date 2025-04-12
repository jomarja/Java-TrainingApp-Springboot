package com.trainingapp.microservice.service;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkloadConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkloadConsumerService.class);
    private final TrainerMongoService trainerMongoService;

    @Autowired
    public WorkloadConsumerService(TrainerMongoService trainerMongoService) {
        this.trainerMongoService = trainerMongoService;
    }

    @KafkaListener(topics = "${kafka.topic.workload}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(TrainerWorkloadRequest request) {
        // Generate or extract a transactionId for logging and tracking purposes
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction {}: Received event for trainer: {}", transactionId, request.getTrainerUsername());

        // Update MongoDB document
        trainerMongoService.updateTrainerSummary(request, transactionId);
    }
}
