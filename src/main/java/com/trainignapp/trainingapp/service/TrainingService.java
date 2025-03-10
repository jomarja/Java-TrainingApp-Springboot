package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.dto.*;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.model.TrainingType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingTypeDao trainingTypeDao;
    private final RemoteTrainingServiceClient remoteTrainingServiceClient;
    String transactionId = UUID.randomUUID().toString();

    @Autowired
    public TrainingService(TrainingDao trainingDao, TrainerDao trainerDao, TraineeDao traineeDao, TrainingTypeDao trainingTypeDao, RemoteTrainingServiceClient remoteTrainingServiceClient) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.trainingTypeDao = trainingTypeDao;
        this.remoteTrainingServiceClient = remoteTrainingServiceClient;
    }

    @Transactional
    public List<TraineeTrainingResponse> getTraineeTrainingsByCriteria(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {

        // Log input parameters
        logger.info("[Transaction ID: {}] Trainee username: {}", transactionId, traineeUsername);
        logger.info("[Transaction ID: {}] Trainer Name: {}", transactionId, trainerName);
        logger.info("[Transaction ID: {}] Training Type: {}", transactionId, trainingType);

        // Ensure trainee exists
        traineeDao.findByUsername(traineeUsername);

        // Fetch trainings
        List<Training> trainings = trainingDao.findByTraineeDate(traineeUsername, fromDate, toDate);
        logger.info("[Transaction ID: {}] Fetched Trainings: {}", transactionId, trainings);

        // Apply filtering logic
        return trainings.stream().filter(training -> {
            boolean matchesTrainer = trainerName == null || training.getTrainer().getUsername().equals(trainerName);
            if (!matchesTrainer) {
                logger.info("[Transaction ID: {}] Filtered out by trainerName: Expected {}, but got {}", transactionId, trainerName, training.getTrainer().getUsername());
                logger.info("[Transaction ID: {}] Training details: {}", transactionId, training);
            }
            return matchesTrainer;
        }).filter(training -> {
            boolean matchesType = trainingType == null || training.getTrainingType().getTrainingTypeName().equals(trainingType);
            if (!matchesType) {
                logger.info("[Transaction ID: {}] Filtered out by trainingType: Expected {}, but got {}", transactionId, trainingType, training.getTrainingType().getTrainingTypeName());
            }
            return matchesType;
        }).map(training -> new TraineeTrainingResponse(training.getTrainingName(), training.getTrainingDate(), training.getTrainingType().getTrainingTypeName(), training.getTrainingDuration(), training.getTrainer().getUsername())).toList();
    }

    @Transactional
    public List<TrainerTrainingResponse> getTrainerTrainingsByCriteria(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        // Log input parameters
        logger.info("[Transaction ID: {}] Trainer Username: {}", transactionId, trainerUsername);
        logger.info("[Transaction ID: {}] From Date: {}", transactionId, fromDate);
        logger.info("[Transaction ID: {}] To Date: {}", transactionId, toDate);
        logger.info("[Transaction ID: {}] Trainee Name: {}", transactionId, traineeName);

        // Ensure the trainer exists
        trainerDao.findByUsername(trainerUsername);

        // Fetch trainings
        List<Training> trainings = trainingDao.findByTrainerDate(trainerUsername, fromDate, toDate);

        // Filter and map the trainings
        List<TrainerTrainingResponse> response = trainings.stream().filter(training -> traineeName == null || training.getTrainee().getUsername().equals(traineeName)).map(training -> new TrainerTrainingResponse(training.getTrainingName(), training.getTrainingDate(), training.getTrainingType().getTrainingTypeName(), training.getTrainingDuration(), training.getTrainee().getUsername())).toList();

        logger.info("[Transaction ID: {}] Filtered Response: {}", transactionId, response);
        return response;
    }

    @Transactional
    public void addTraining(String traineeUsername, String trainerUsername, String trainingName, Date trainingDate, Integer trainingDuration, String trainingTypeName) {
        String txnId = UUID.randomUUID().toString();
        logger.info("[Transaction ID: {}] addTraining called with traineeUsername={}, trainerUsername={}, trainingName={}, trainingDate={}, trainingDuration={}, trainingTypeName={}", txnId, traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration, trainingTypeName);
        Trainee trainee = traineeDao.findByUsername(traineeUsername).orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));
        Trainer trainer = trainerDao.findByUsername(trainerUsername).orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));
        TrainingType trainingType = trainingTypeDao.findByName(trainingTypeName).orElseThrow(() -> new EntityNotFoundException("Training type not found: " + trainingTypeName));
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(trainingName);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        trainingDao.save(training);
        logger.info("[Transaction ID: {}] Training added for trainee: {}", txnId, trainee.getUsername());
        TrainerWorkloadRequest workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setTrainerUsername(trainer.getUsername());
        workloadRequest.setTrainerFirstName(trainer.getFirstName());
        workloadRequest.setTrainerLastName(trainer.getLastName());
        workloadRequest.setActive(trainer.getIsActive());
        workloadRequest.setTrainingDate(trainingDate);
        workloadRequest.setTrainingDuration(trainingDuration);
        workloadRequest.setActionType("ADD");
        logger.info("[Transaction ID: {}] Workload request built: {}", txnId, workloadRequest);
        remoteTrainingServiceClient.callUpdateWorkloadWithTransaction(txnId, workloadRequest);
        logger.info("[Transaction ID: {}] Remote workload update called", txnId);
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {

        List<Trainer> allTrainers = trainerDao.findAll();
        logger.info("[Transaction ID: {}] successfully got Unassigned Trainers For Trainee: {}", transactionId, traineeUsername);
        return allTrainers.stream().filter(trainer -> trainingDao.findByUsernames(trainer.getUsername(), traineeUsername).isEmpty()).toList();
    }

    @Transactional
    public List<TrainerDetails> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        logger.info("[Transaction ID: {}] update Trainers for Trainee: {}", transactionId, traineeUsername);
        // Fetch trainee
        Trainee trainee = traineeDao.findByUsername(traineeUsername).orElseThrow(() -> new RuntimeException("Trainee not found"));
        logger.info("[Transaction ID: {}] get Unassigned Trainers For Trainee: {}", transactionId, traineeUsername);
        // Fetch trainers
        List<Trainer> trainers = trainerDao.findAll().stream().filter(trainer -> trainerUsernames.contains(trainer.getUsername())).toList();

        // Delete existing trainings
        List<Training> existingTrainings = trainingDao.findByTraineeUsername(traineeUsername);
        trainingDao.deleteAll(existingTrainings);
        logger.info("[Transaction ID: {}] Deleted Old Training: {}", transactionId, traineeUsername);
        // Assign new trainings
        List<Training> newTrainings = trainers.stream().flatMap(trainer -> existingTrainings.stream().map(existingTraining -> {
            Training newTraining = new Training();
            newTraining.setTrainee(trainee);
            newTraining.setTrainer(trainer);
            newTraining.setTrainingName(existingTraining.getTrainingName());
            newTraining.setTrainingType(existingTraining.getTrainingType());
            newTraining.setTrainingDuration(existingTraining.getTrainingDuration());
            newTraining.setTrainingDate(new java.sql.Date(System.currentTimeMillis()));
            return newTraining;
        })).toList();
        logger.info("[Transaction ID: {}] Created New Training: {}", transactionId, traineeUsername);

        trainingDao.saveAll(newTrainings);
        logger.info("[Transaction ID: {}] All New Trainings Saved: {}", transactionId, trainee.getFirstName());
        return trainers.stream().map(trainer -> new TrainerDetails(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization().getTrainingTypeName())).toList();
    }

    public Training select(String name) {
        return trainingDao.select(name).orElseThrow(() -> new RuntimeException("Training not found with name: " + name));
    }

    @Transactional
    public void cancelTraining(CancelTrainingRequest request) {
        String txnId = UUID.randomUUID().toString();
        logger.info("[Transaction ID: {}] cancelTraining called with request: {}", txnId, request);
        if (request.getTrainingDate().before(new Date())) {
            logger.error("[Transaction ID: {}] Cannot cancel training that has occurred", txnId);
            throw new IllegalArgumentException("Cannot cancel a training session that has already occurred.");
        }
        Training training = trainingDao.selectByNameAndDate(request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration(), request.getTrainingType(), request.getTrainerUsername(), request.getTraineeUsername()).orElseThrow(() -> new EntityNotFoundException("Training not found with the provided criteria"));
        trainingDao.delete(training);
        logger.info("[Transaction ID: {}] Training cancelled: {}", txnId, request.getTrainingName());
        Trainer trainer = training.getTrainer();
        TrainerWorkloadRequest workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setTrainerUsername(trainer.getUsername());
        workloadRequest.setTrainerFirstName(trainer.getFirstName());
        workloadRequest.setTrainerLastName(trainer.getLastName());
        workloadRequest.setActive(trainer.getIsActive());
        workloadRequest.setTrainingDate(training.getTrainingDate());
        workloadRequest.setTrainingDuration(training.getTrainingDuration());
        workloadRequest.setActionType("DELETE");
        remoteTrainingServiceClient.callUpdateWorkloadWithTransaction(txnId, workloadRequest);
        logger.info("[Transaction ID: {}] Remote workload update called for cancellation", txnId);
    }
}
