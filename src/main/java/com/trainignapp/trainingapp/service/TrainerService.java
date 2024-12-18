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

        trainer.setUsername(username);
        trainer.setPassword(password);

        trainerDao.save(trainer);
        logger.info("trainee created", trainer.getUsername());
    }

    public boolean authenticateTrainer(String username, String password) {
        if (trainerDao.findByUsername(username).isPresent()) {
            return trainerDao.findByUsername(username).get().getPassword().equals(password);
        } else throw new RuntimeException("Wrong username");

    }

    public Trainer select(String username) {
        return trainerDao.findByUsername(username).orElse(null);
    }

    public void updateTrainerPassword(String username, String newPassword) {
        Trainer trainer = select(username);
        if (authenticateTrainer(username, trainer.getPassword())) {
            trainer.setPassword(newPassword);
            trainerDao.save(trainer);
        } else new RuntimeException("Authentificate First Please");

    }

    public void updateProfile(String username, Trainer newtrainer) {
        Trainer oldTrainer = select(username);
        if (authenticateTrainer(username, newtrainer.getPassword())) {
            oldTrainer.setSpecialization(newtrainer.getSpecialization());
            oldTrainer.setUsername(newtrainer.getUsername());
            oldTrainer.setFirstName(newtrainer.getFirstName());
            oldTrainer.setLastName(newtrainer.getLastName());
            oldTrainer.setPassword(newtrainer.getPassword());
            oldTrainer.setIsActive(newtrainer.getIsActive());
            trainerDao.save(oldTrainer);
            logger.info("trainer profile updated", newtrainer);
        } else new RuntimeException("Authentificate First Please");
    }

    public void deactivateTrainer(String username) {
        Trainer trainer = select(username);
        if (!authenticateTrainer(username, trainer.getPassword())) {
            new RuntimeException("Authentificate First Please");
        } else {
            trainer.setIsActive(!trainer.getIsActive());
            trainerDao.save(trainer);
            logger.info("trainer active status changed to: ", trainer.getIsActive());

        }
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
        for (Trainer trainee : trainerDao.findAll()) {
            if (trainee.getUsername().equals(username)) {
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
