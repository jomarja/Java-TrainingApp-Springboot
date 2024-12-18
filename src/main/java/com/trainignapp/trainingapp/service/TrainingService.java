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
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    @Autowired
    private TrainingDao trainingDao;
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private TrainingTypeDao trainingTypeDao;


    @Transactional
    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));


        List<Training> trainings = trainingDao.findByDate(
                traineeUsername, fromDate, toDate);

        // Additional filtering for trainer name and training type
        return trainings.stream()
                .filter(training -> (trainerName == null || training.getTrainer().getUsername().equals(trainerName)))
                .filter(training -> (trainingType == null || training.getTrainingType().getTrainingTypeName().equals(trainingType)))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Training> getTrainerTrainingsByCriteria(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));


        List<Training> trainings = trainingDao.findByDate(
                trainerUsername, fromDate, toDate);

        // Additional filtering for trainee name
        return trainings.stream()
                .filter(training -> (traineeName == null || training.getTrainee().getUsername().equals(traineeName)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTraining(String traineeUsername, String trainerUsername, String trainingTypeName, Training training) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        TrainingType trainingType = trainingTypeDao.findByName(trainingTypeName)
                .orElseThrow(() -> new RuntimeException("Training Type not found"));

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        trainingDao.save(training);
        logger.info("training added", trainee.getFirstName());
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        // Fetch all active trainers
        List<Trainer> allTrainers = trainerDao.findAll();

        // Filter trainers not linked to this trainee
        return allTrainers.stream()
                .filter(trainer -> trainingDao.findByUsernames(
                        trainer.getUsername(), traineeUsername).isEmpty())
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        // Fetch trainee
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        // Fetch trainers
        List<Trainer> trainers = trainerDao.findAll().stream()
                .filter(trainer -> trainerUsernames.contains(trainer.getUsername()))
                .collect(Collectors.toList());

        // Delete existing trainings
        List<Training> existingTrainings = trainingDao.findByTraineeUsername(traineeUsername);
        trainingDao.deleteAll(existingTrainings);

        // Assign new trainings
        List<Training> newTrainings = trainers.stream()
                .map(trainer -> {
                    Training training = new Training();
                    training.setTrainee(trainee);
                    training.setTrainer(trainer);
                    training.setTrainingDate(new java.sql.Date(System.currentTimeMillis()));
                    training.setTrainingName("Updated Training");
                    training.setTrainingDuration(1); // Default duration
                    return training;
                }).collect(Collectors.toList());

        trainingDao.saveAll(newTrainings);
        logger.info("all new trainings saved", trainee.getFirstName());
    }

    public Training select(String name) {
        return trainingDao.select(name).get();
    }
}
