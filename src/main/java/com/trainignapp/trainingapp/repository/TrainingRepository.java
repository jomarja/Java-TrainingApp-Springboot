package com.trainignapp.trainingapp.repository;

import com.trainignapp.trainingapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findByTrainee_UsernameAndTrainingDateBetween(
            String traineeUsername, Date fromDate, Date toDate);

    List<Training> findByTrainer_UsernameAndTrainee_Username(
            String trainerUsername, String traineeUsername);

    Optional<Training> findBytrainingName(String name);

    List<Training> findByTrainee_Username(String username);
}
