package com.trainingapp.microservice.service;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WorkloadConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkloadConsumerService.class);
    private final TrainerWorkloadService trainerWorkloadService;

    @Autowired
    public WorkloadConsumerService(TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @KafkaListener(topics = "${kafka.topic.workload}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(TrainerWorkloadRequest request) {
        logger.info("[Received from Kafka: {}]", request.getTrainerUsername());
        trainerWorkloadService.updateWorkload(request);
    }
}
