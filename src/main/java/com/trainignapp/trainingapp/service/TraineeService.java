package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dto.TraineeProfileResponse;
import com.trainignapp.trainingapp.dto.TraineeTrainerResponse;
import com.trainignapp.trainingapp.dto.TraineeUpdatedProfileResponse;
import com.trainignapp.trainingapp.dto.UpdateTraineeProfileRequest;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    Random random;
    String transactionId = UUID.randomUUID().toString();
    private final Counter createdTraineesCounter;

    @Autowired
    public TraineeService(TraineeDao traineeDao, TrainerDao trainerDao, TrainingDao trainingDao, MeterRegistry meterRegistry) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
        this.createdTraineesCounter = meterRegistry.counter("trainee.created.count");
    }

    public void createTrainee(Trainee trainee) {
        logger.info("[Transaction ID: {}] Starting Creating process for Trainee: {}", transactionId, trainee);
        String username = generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(password);

        if (trainerDao.findByUsername(trainee.getUsername()).isPresent()) {
            logger.info("[Transaction ID: {}] Starting startChecking if trainee already exists: {}", transactionId, trainee);
            throw new IllegalArgumentException("This user is already registered as a trainer and cannot register as a trainee.");
        }
        traineeDao.save(trainee);
        logger.info("[Transaction ID: {}] Created Trainee: {}", transactionId, trainee);
        createdTraineesCounter.increment();
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeDao.findByUsername(username).map(trainee -> trainee.getPassword().equals(password)).orElse(false);
    }

    public Trainee select(String username) {
        return traineeDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
    }

    public void updateTraineePassword(String username, String newPassword) {
        Trainee trainer = select(username);
        if (authenticateTrainee(username, trainer.getPassword())) {
            trainer.setPassword(newPassword);
            traineeDao.save(trainer);
        } else {
            throw new IllegalArgumentException("Authenticate First Please: {}");
        }
    }

    public TraineeUpdatedProfileResponse updateProfile(String username, UpdateTraineeProfileRequest request) {
        logger.info("[Transaction ID: {}]  Updating profile for Trainee: {}", transactionId, username);
        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        if (!authenticateTrainee(username, trainee.getPassword())) {
            logger.info("[Transaction ID: {}] Authenticating Trainee: {}", transactionId, trainee);
            throw new IllegalArgumentException("Authenticate First Please: {}");
        }

        trainee.setUsername(generateUniqueUsername(request.getFirstName(), request.getLastName()));
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        trainee.setIsActive(request.getIsActive());

        traineeDao.save(trainee);
        logger.info("[Transaction ID: {}] Changes saved for Trainee: {}", transactionId, trainee);

        logger.info("[Transaction ID: {}] profile updated for Trainee: {}", transactionId, trainee);

        // Fetch the trainers linked to this trainee via Training
        List<TraineeTrainerResponse> trainers = trainingDao.findByTraineeUsername(username).stream().map(training -> new TraineeTrainerResponse(training.getTrainer().getUsername(), training.getTrainer().getFirstName(), training.getTrainer().getLastName(), training.getTrainer().getSpecialization().getTrainingTypeName())).toList();

        return new TraineeUpdatedProfileResponse(trainee.getUsername(), trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress(), trainee.getIsActive(), trainers);
    }

    public void deactivateTrainee(String username, boolean isActive) {
        logger.info("[Transaction ID: {}] Starting deactivation process for Trainee: {}", transactionId, username);
        Trainee trainer = select(username);
        logger.info("[Transaction ID: {}] Fetched Trainee: {}", transactionId, trainer);
        if (!authenticateTrainee(username, trainer.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        } else {
            logger.info("[Transaction ID: {}] Authentication successful for Trainee: {}", transactionId, username);
            if (Boolean.TRUE.equals(trainer.getIsActive()) != isActive) {
                trainer.setIsActive(!trainer.getIsActive());

                traineeDao.save(trainer);
                logger.info("Trainee active status changed: {}", trainer.getIsActive());
            } else {
                logger.warn("[Transaction ID: {}] No status change needed. Current status is already: {}", transactionId, isActive);
                throw new IllegalArgumentException("The status is already" + isActive + ": {}");
            }
        }
    }

    @Transactional
    public void deleteTrainee(String username) {
        logger.info("[Transaction ID: {}] deleting Trainee: {}", transactionId, username);
        Trainee trainee = select(username);
        if (authenticateTrainee(username, trainee.getPassword())) {
            logger.info("[Transaction ID: {}] Authenticated Trainee: {}", transactionId, username);
            // Delete all trainings associated with this trainee
            traineeDao.delete(trainee);
            logger.info("[Transaction ID: {}] deleted Trainee: {}", transactionId, username);
        } else {
            logger.error("[Transaction ID: {}] Authentication failed for Trainee: {}", transactionId, username);
            throw new IllegalArgumentException("Authenticate please: {}");
        }
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        random = new Random();

        while (isUsernameTaken(username)) {

            int randomNumber = random.nextInt(1000);
            username = baseUsername + randomNumber;
        }
        logger.info("Uname is: {}", username);
        return username;
    }

    private boolean isUsernameTaken(String username) {
        for (Trainee trainee : traineeDao.findAll()) {
            if (trainee.getUsername().equals(username)) {
                logger.info("Uname Taken: {}", trainee.getUsername());
                return true;
            }
        }
        return false;
    }

    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    int length = 10;

    public String generateRandomPassword() {
        random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    public TraineeProfileResponse getTraineeProfile(String username) {
        logger.info("[Transaction ID: {}] Getting profile for Trainee: {}", transactionId, username);

        // Fetch trainee using the username
        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainee not found with username: " + username));

        if (!authenticateTrainee(username, trainee.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        }

        // Fetch all trainings associated with this trainee
        List<Training> trainings = trainingDao.findByTraineeUsername(trainee.getUsername());
        logger.info("[Transaction ID: {}] Fetched Trainings: {}", transactionId, trainings);

        // Map trainings to a simplified trainer response list
        List<TraineeTrainerResponse> trainers = trainings.stream().map(training -> {
            Trainer trainer = training.getTrainer();
            return new TraineeTrainerResponse(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), training.getTrainingType().getTrainingTypeName());
        }).toList();
        logger.info("[Transaction ID: {}] Fetched successfully: {}", transactionId, trainers);

        // Build and return the trainee profile response
        return new TraineeProfileResponse(trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress(), trainee.getIsActive(), trainers);
    }
}
