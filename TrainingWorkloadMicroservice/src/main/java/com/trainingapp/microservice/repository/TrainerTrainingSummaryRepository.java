package com.trainingapp.microservice.repository;

import com.trainingapp.microservice.mongo.TrainerTrainingSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummary, String> {
    TrainerTrainingSummary findByTrainerUsername(String trainerUsername);
}
