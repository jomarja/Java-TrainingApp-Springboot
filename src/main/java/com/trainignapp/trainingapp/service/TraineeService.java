package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDao;

    @Autowired
    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainee(Trainee trainee) {
        logger.info("Creating a new trainee: {}", trainee.getUsername());
        String username = generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(password);

        traineeDao.save(trainee);
        logger.info("Trainee Created: {}", trainee.getUsername());
    }

    public boolean authenticateTrainee(String username, String password) {
        if (traineeDao.findByUsername(username).isPresent()) {
            return traineeDao.findByUsername(username).get().getPassword().equals(password);
        } else {
            return false;
        }
    }

    public Trainee select(String username) {
        return traineeDao.findByUsername(username).orElse(null);
    }

    public void updateTraineePassword(String username, String newPassword) {
        Trainee trainer = select(username);
        if (authenticateTrainee(username, trainer.getPassword())) {
            trainer.setPassword(newPassword);
            traineeDao.save(trainer);
        } else {
            throw new RuntimeException("Authentificate First Please");
        }
    }

    public void updateProfile(String username, Trainee newtrainee) {
        Trainee oldTrainer = select(username);
        if (authenticateTrainee(username, newtrainee.getPassword())) {
            oldTrainer.setUsername(newtrainee.getUsername());
            oldTrainer.setFirstName(newtrainee.getFirstName());
            oldTrainer.setLastName(newtrainee.getLastName());
            oldTrainer.setPassword(newtrainee.getPassword());
            oldTrainer.setIsActive(newtrainee.getIsActive());
            traineeDao.save(oldTrainer);
            logger.info("Trainee Profile Updated: {}", newtrainee.getUsername());
        } else {
            new RuntimeException("Authentificate First Please");
        }
    }

    public void deactivateTrainee(String username) {
        Trainee trainer = select(username);
        if (!authenticateTrainee(username, trainer.getPassword())) {
            new RuntimeException("Authentificate First Please");
        } else {
            trainer.setIsActive(!trainer.getIsActive());
            traineeDao.save(trainer);
            logger.info("Trainee active status changed: {}", trainer.getIsActive());
        }
    }

    public void deleteTrainee(String username) {
        Trainee trainee = select(username);
        if (authenticateTrainee(username, trainee.getPassword())) {
            traineeDao.delete(trainee);
            logger.info("Trainee deleted: {}", trainee.getUsername());
        } else {
            throw new RuntimeException("Authentificate please: {}");
        }

    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        Random random = new Random();

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

    public String generateRandomPassword() {
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
