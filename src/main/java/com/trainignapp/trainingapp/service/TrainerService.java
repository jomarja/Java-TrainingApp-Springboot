package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dto.TrainerProfileResponse;
import com.trainignapp.trainingapp.dto.TrainerProfileResponseFull;
import com.trainignapp.trainingapp.dto.TrainerTraineeResponse;
import com.trainignapp.trainingapp.dto.UpdateTrainerProfileRequest;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingDao trainingDao;
    Random random = new Random();
    String transactionId = UUID.randomUUID().toString();

    @Autowired
    public TrainerService(TrainerDao trainerDao, TraineeDao traineeDao, TrainingDao trainingDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.trainingDao = trainingDao;
    }

    public void createTrainer(Trainer trainer) {
        logger.info("[Transaction ID: {}] Starting Creating process for Trainee: {}", transactionId, trainer);
        String username = generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = generateRandomPassword();

        trainer.setUsername(username);
        trainer.setPassword(password);

        logger.info("[Transaction ID: {}] Checking if User already exists: {}", transactionId, trainer);
        if (traineeDao.findByUsername(trainer.getUsername()).isPresent()) {
            throw new IllegalArgumentException("This user is already registered as a trainee and cannot register as a trainer.");
        }
        logger.info("[Transaction ID: {}] Checking if Specialization exists: {}", transactionId, trainer);

        trainerDao.save(trainer);
        logger.info("[Transaction ID: {}] Trainer Created Successfully: {}", transactionId, trainer);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerDao.findByUsername(username).map(trainer -> trainer.getPassword().equals(password)).orElseThrow(() -> new IllegalArgumentException("Wrong username"));
    }

    public Trainer select(String username) {
        return trainerDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
    }

    public void updateTrainerPassword(String username, String newPassword) {
        Trainer trainer = select(username);
        if (authenticateTrainer(username, trainer.getPassword())) {
            trainer.setPassword(newPassword);
            trainerDao.save(trainer);
        } else {
            throw new IllegalArgumentException("Authenticate First Please: {}");
        }
    }

    public TrainerProfileResponseFull updateProfile(String username, UpdateTrainerProfileRequest newTrainer) {
        logger.info("[Transaction ID: {}] Updating profile for Trainer: {}", transactionId, username);
        // Fetch the existing trainer
        Trainer oldTrainer = select(username);

        // Ensure authentication
        if (!authenticateTrainer(username, oldTrainer.getPassword())) {
            logger.info("[Transaction ID: {}] Authentication Failed for Trainer: {}", transactionId, username);
            throw new IllegalArgumentException("Authenticate First Please: {}");
        }

        logger.info("[Transaction ID: {}] Authenticated Successfully for Trainer: {}", transactionId, username);

        // Update fields
        oldTrainer.setUsername(generateUniqueUsername(oldTrainer.getFirstName(), oldTrainer.getLastName()));
        oldTrainer.setFirstName(newTrainer.getFirstName());
        oldTrainer.setLastName(newTrainer.getLastName());
        oldTrainer.setIsActive(newTrainer.getIsActive());

        // Save changes
        trainerDao.save(oldTrainer);

        // Log the update
        logger.info("[Transaction ID: {}] Profile Updated Successfully for Trainer: {}", transactionId, username);
        List<TrainerTraineeResponse> trainees = trainingDao.findByTrainerUsername(username).stream().map(training -> {
            Trainee trainee = training.getTrainee();
            return new TrainerTraineeResponse(trainee.getUsername(), trainee.getFirstName(), trainee.getLastName());
        }).toList();

        return new TrainerProfileResponseFull(oldTrainer.getUsername(), oldTrainer.getFirstName(), oldTrainer.getLastName(), oldTrainer.getSpecialization().getTrainingTypeName(), oldTrainer.getIsActive(), trainees);
    }

    public void deactivateTrainer(String username, boolean isActive) {
        logger.info("[Transaction ID: {}] Deactivating Trainer: {}", transactionId, username);
        Trainer trainer = select(username);

        // Ensure the trainer is authenticated
        if (!authenticateTrainer(username, trainer.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        } else {
            logger.info("[Transaction ID: {}] Authenticated successfully for Trainer: {}", transactionId, username);
            if (Boolean.TRUE.equals(trainer.getIsActive()) != isActive) {
                trainer.setIsActive(isActive); // Update the active status
                trainerDao.save(trainer);
                logger.info("[Transaction ID: {}] Trainer Active status changed Successfully: {}", transactionId, username);
            } else {
                throw new IllegalArgumentException("The status is already" + isActive + ": {}");
            }
        }
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        // Check for uniqueness and add a random number suffix if necessary
        while (isUsernameTaken(username)) {
            int randomNumber = random.nextInt(1000); // Generate a random number (0-999)
            username = baseUsername + randomNumber;
        }
        return username;
    }

    public TrainerProfileResponse getTrainerProfile(String username) {
        logger.info("[Transaction ID: {}] getting profile for Trainer: {}", transactionId, username);
        Trainer trainer = trainerDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));

        if (!authenticateTrainer(username, trainer.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        }

        List<TrainerTraineeResponse> trainees = trainingDao.findByTrainerUsername(username).stream().map(training -> new TrainerTraineeResponse(training.getTrainee().getUsername(), training.getTrainee().getFirstName(), training.getTrainee().getLastName())).distinct().toList();
        logger.info("[Transaction ID: {}] Successfully got profile for Trainer: {}", transactionId, username);
        return new TrainerProfileResponse(trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization().getTrainingTypeName(), trainer.getIsActive(), trainees);
    }

    private boolean isUsernameTaken(String username) {
        for (Trainer trainee : trainerDao.findAll()) {
            if (trainee.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String generateRandomPassword() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(character.length());
            stringBuilder.append(character.charAt(index));
        }

        return stringBuilder.toString();
    }
}
