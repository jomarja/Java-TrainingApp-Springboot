//package com.trainignapp.trainingapp;
//
//import com.trainignapp.trainingapp.Storage.StorageBean;
//import com.trainignapp.trainingapp.model.Trainee;
//import com.trainignapp.trainingapp.model.Trainer;
//import com.trainignapp.trainingapp.model.Training;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class StorageBeanTest {
//
//    @Autowired
//    private StorageBean storageBean;
//
//    @Test
//    public void testTraineesLoadedFromPropertiesFile() {
//        Map<Integer, Trainee> trainees = storageBean.getTrainees();
//
//        // Assert trainee map is not empty
//        assertFalse(trainees.isEmpty(), "Trainee map should not be empty");
//
//        // Assert specific trainee data
//        Trainee trainee = trainees.get(1);
//        assertNotNull(trainee, "Trainee with ID 1 should exist");
//        assertEquals("John", trainee.getFirstName());
//        assertEquals("Doe", trainee.getLastName());
//    }
//
//    @Test
//    public void testTrainersLoadedFromPropertiesFile() {
//        Map<Integer, Trainer> trainers = storageBean.getTrainers();
//
//        // Assert trainer map is not empty
//        assertFalse(trainers.isEmpty(), "Trainer map should not be empty");
//
//        // Assert specific trainer data
//        Trainer trainer = trainers.get(1);
//        assertNotNull(trainer, "Trainer with ID 1 should exist");
//        assertEquals("Alice", trainer.getFirstName());
//        assertEquals("Smith", trainer.getLastName());
//    }
//
//    @Test
//    public void testTrainingsLoadedFromPropertiesFile() {
//        Map<String, Training> trainings = storageBean.getTrainings();
//
//        // Assert training map is not empty
//        assertFalse(trainings.isEmpty(), "Training map should not be empty");
//
//        // Assert specific training data
//        Training training = trainings.get("training1");
//        assertNotNull(training, "Training with key 'training1' should exist");
//        assertEquals("gevorq", training.getName());
//    }
//}
//
