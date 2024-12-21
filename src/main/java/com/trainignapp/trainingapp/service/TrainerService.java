package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDao;
    Random random;

    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public void createTrainer(Trainer trainer) {
        logger.info("Creating a new trainer: {}", trainer.getUsername());
        String username = generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = generateRandomPassword();

        trainer.setUsername(username);
        trainer.setPassword(password);

        trainerDao.save(trainer);
        logger.info("Trainer Created: {}", trainer.getUsername());
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerDao.findByUsername(username).map(trainer -> trainer.getPassword().equals(password)).orElseThrow(() -> new IllegalArgumentException("Wrong username"));
    }

    public Trainer select(String username) {
        return trainerDao.findByUsername(username).orElse(null);
    }

    public void updateTrainerPassword(String username, String newPassword) {
        Trainer trainer = select(username);
        if (authenticateTrainer(username, trainer.getPassword())) {
            trainer.setPassword(newPassword);
            trainerDao.save(trainer);
        } else {
            throw new IllegalArgumentException("Authentificate First Please: {}");
        }
    }

    public void updateProfile(String username, Trainer newTrainer) {
        // Fetch the existing trainer
        Trainer oldTrainer = select(username);

        // Ensure authentication
        if (!authenticateTrainer(username, newTrainer.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        }

        // Update fields
        oldTrainer.setSpecialization(newTrainer.getSpecialization());
        oldTrainer.setUsername(newTrainer.getUsername());
        oldTrainer.setFirstName(newTrainer.getFirstName());
        oldTrainer.setLastName(newTrainer.getLastName());
        oldTrainer.setPassword(newTrainer.getPassword());
        oldTrainer.setIsActive(newTrainer.getIsActive());

        // Save changes
        trainerDao.save(oldTrainer);

        // Log the update
        logger.info("Trainer profile updated successfully for username: {}", oldTrainer.getUsername());
    }

    public void deactivateTrainer(String username) {
        Trainer trainer = select(username);

        // Ensure the trainer is authenticated
        if (!authenticateTrainer(username, trainer.getPassword())) {
            throw new IllegalArgumentException("Authenticate First Please");
        }

        // Toggle the 'isActive' status
        trainer.setIsActive(!trainer.getIsActive());

        // Save the updated trainer
        trainerDao.save(trainer);

        // Log the updated status
        logger.info("Trainer active status changed: {}", trainer.getIsActive());
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        random = new Random();

        // Check for uniqueness and add a random number suffix if necessary
        while (isUsernameTaken(username)) {
            int randomNumber = random.nextInt(1000); // Generate a random number (0-999)
            username = baseUsername + randomNumber;
        }
        return username;
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
        String charachter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;
        random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charachter.length());
            stringBuilder.append(charachter.charAt(index));
        }

        return stringBuilder.toString();
    }
}
