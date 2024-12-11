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
    private TraineeDao traineeDao;


    @Autowired
    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainee(Trainee trainee) {
        logger.info("Creating a new trainee: {}", trainee.getFirstName());
        String username = generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generateRandomPassword();

        trainee.setUserName(username);
        trainee.setPassword(password);

        traineeDao.create(trainee);
    }

    public void updateTrainee(Trainee trainee) {
        traineeDao.update(trainee);
    }

    public void deleteTrainee(int userId) {
        traineeDao.delete(userId);
    }

    public Trainee getTrainee(int userId) {
        return traineeDao.select(userId);
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        Random random = new Random();


        while (isUsernameTaken(username)) {

            int randomNumber = random.nextInt(1000);
            username = baseUsername + randomNumber;
        }
        logger.info("The uname is", username);
        return username;
    }

    private boolean isUsernameTaken(String username) {
        for (Trainee trainee : traineeDao.getAllTrainees()) {
            if (trainee.getUserName().equals(username)) {
                logger.info("uname taken:", true);
                return true;
            }
        }
        logger.info("uname taken:", false);
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
