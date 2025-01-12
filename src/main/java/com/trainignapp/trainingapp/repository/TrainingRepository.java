package com.trainignapp.trainingapp.repository;

import com.trainignapp.trainingapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("SELECT t FROM Training t WHERE t.trainee.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate")
    List<Training> findByTraineeUsernameAndDateRange(@Param("username") String username, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("SELECT t FROM Training t WHERE t.trainer.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate")
    List<Training> findByTrainerUsernameAndDateRange(@Param("username") String username, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    List<Training> findByTrainer_UsernameAndTrainee_Username(String trainerUsername, String traineeUsername);

    Optional<Training> findBytrainingName(String name);

    List<Training> findByTrainee_Username(String username);

    List<Training> findByTrainer_Username(String username);
}
