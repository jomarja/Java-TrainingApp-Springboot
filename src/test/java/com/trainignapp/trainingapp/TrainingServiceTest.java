package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.service.TrainingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainingServiceTest {
    @Test
    public void testCreateTraining() {
        TrainingDao dao= new TrainingDao();
        TrainingService service = new TrainingService(dao);

        Training training = new Training();
        training.setType("Running");
        training.setName("CrazyRun");
        training.setTrainer_id(123);
        training.setTrainee_id(123);

        service.createTraining(training);

        Training fetched = dao.select("CrazyRun");
        assertNotNull(fetched);
        assertEquals("CrazyRun", fetched.getName());
    }
}
