package com.eurika.server.trainingapp.service;

import com.eurika.server.trainingapp.dto.TrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate;
    @Value("${kafka.topic.workload}")
    private String workloadTopic;

    public KafkaProducerService(KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTrainerWorkloadUpdate(TrainerWorkloadRequest request) {
        // Optionally, add the transaction ID to the message as a header if needed.
        kafkaTemplate.send(workloadTopic, request);
    }
}
