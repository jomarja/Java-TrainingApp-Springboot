package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TrainingDao {
    private final TrainingRepository repository;

    @Autowired
    public TrainingDao(TrainingRepository repository) {
        this.repository = repository;
    }

    public List<Training> findByTraineeDate(String username, Date fromDate, Date toDate) {
        return repository.findByTraineeUsernameAndDateRange(username, fromDate, toDate);
    }

    public List<Training> findByTrainerDate(String username, Date fromDate, Date toDate) {
        return repository.findByTrainerUsernameAndDateRange(username, fromDate, toDate);
    }

    public List<Training> findByUsernames(String trainerUsername, String traineeUsername) {
        return repository.findByTrainer_UsernameAndTrainee_Username(trainerUsername, traineeUsername);
    }

    public void save(Training training) {
        repository.save(training);
    }

    public void saveAll(List<Training> trainings) {
        repository.saveAll(trainings);
    }

    public void deleteAll(List<Training> training) {
        repository.deleteAll(training);
    }

    public List<Training> findByTraineeUsername(String username) {
        return repository.findByTrainee_Username(username);
    }

    public List<Training> findByTrainerUsername(String username) {
        return repository.findByTrainer_Username(username);
    }

    public Optional<Training> select(String trainingName) {
        return repository.findBytrainingName(trainingName);
    }
}
