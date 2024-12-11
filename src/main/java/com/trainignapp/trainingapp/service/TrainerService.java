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
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private TrainerDao trainerDao;


    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;


    }

    public void createTrainer(Trainer trainer) {
        logger.info("Creating a new trainer: {}", trainer.getFirstName());
        String username = generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = generateRandomPassword();

        trainer.setUserName(username);
        trainer.setPassword(password);

        trainerDao.create(trainer);
    }

    public void updateTrainer(Trainer trainer) {
        trainerDao.update(trainer);
    }


    public Trainer getTrainer(int userId) {
        return trainerDao.select(userId);
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        Random random = new Random();

        // Check for uniqueness and add a random number suffix if necessary
        while (isUsernameTaken(username)) {
            int randomNumber = random.nextInt(1000); // Generate a random number (0-999)
            username = baseUsername + randomNumber;
        }
        return username;
    }

    private boolean isUsernameTaken(String username) {
        for (Trainer trainee : trainerDao.getAllTrainers()) {
            if (trainee.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private String generateRandomPassword() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }
}
