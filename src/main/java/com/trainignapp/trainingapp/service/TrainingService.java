package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.model.TrainingType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainingService(TrainingDao trainingDao, TrainerDao trainerDao, TraineeDao traineeDao, TrainingTypeDao trainingTypeDao) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.trainingTypeDao = trainingTypeDao;
    }

    @Transactional
    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        traineeDao.findByUsername(traineeUsername);

        List<Training> trainings = trainingDao.findByDate(traineeUsername, fromDate, toDate);

        // Additional filtering for trainer name and training type
        return trainings.stream().filter(training -> (trainerName == null || training.getTrainer().getUsername().equals(trainerName))).filter(training -> (trainingType == null || training.getTrainingType().getTrainingTypeName().equals(trainingType))).toList();
    }

    @Transactional
    public List<Training> getTrainerTrainingsByCriteria(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        trainerDao.findByUsername(trainerUsername);

        List<Training> trainings = trainingDao.findByDate(trainerUsername, fromDate, toDate);

        // Additional filtering for trainee name
        return trainings.stream().filter(training -> (traineeName == null || training.getTrainee().getUsername().equals(traineeName))).toList();
    }

    @Transactional
    public void addTraining(String traineeUsername, String trainerUsername, String trainingTypeName, Training training) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername).orElseThrow(() -> new RuntimeException("Trainee not found"));
        Trainer trainer = trainerDao.findByUsername(trainerUsername).orElseThrow(() -> new RuntimeException("Trainer not found"));
        TrainingType trainingType = trainingTypeDao.findByName(trainingTypeName).orElseThrow(() -> new RuntimeException("Training Type not found"));

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        trainingDao.save(training);
        logger.info("Training added: {}", trainee.getUsername());
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        // Fetch all active trainers
        List<Trainer> allTrainers = trainerDao.findAll();

        // Filter trainers not linked to this trainee
        return allTrainers.stream().filter(trainer -> trainingDao.findByUsernames(trainer.getUsername(), traineeUsername).isEmpty()).toList();
    }

    @Transactional
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        // Fetch trainee
        Trainee trainee = traineeDao.findByUsername(traineeUsername).orElseThrow(() -> new RuntimeException("Trainee not found"));

        // Fetch trainers
        List<Trainer> trainers = trainerDao.findAll().stream().filter(trainer -> trainerUsernames.contains(trainer.getUsername())).toList();

        // Delete existing trainings
        List<Training> existingTrainings = trainingDao.findByTraineeUsername(traineeUsername);
        trainingDao.deleteAll(existingTrainings);

        // Assign new trainings
        List<Training> newTrainings = trainers.stream().map(trainer -> {
            Training training = new Training();
            training.setTrainee(trainee);
            training.setTrainer(trainer);
            training.setTrainingDate(new java.sql.Date(System.currentTimeMillis()));
            training.setTrainingName("Updated Training");
            training.setTrainingDuration(1); // Default duration
            return training;
        }).toList();

        trainingDao.saveAll(newTrainings);
        logger.info("All New Trainings Saved: {}", trainee.getFirstName());
    }

    public Training select(String name) {
        return trainingDao.select(name).orElseThrow(() -> new RuntimeException("Training not found with name: " + name));
    }
}
