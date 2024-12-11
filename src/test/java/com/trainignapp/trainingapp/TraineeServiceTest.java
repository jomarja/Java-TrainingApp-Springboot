package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.service.TraineeService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeServiceTest {
    @Test
    public void testCreateTrainee() {
        TraineeDao dao = new TraineeDao();
        TraineeService service = new TraineeService(dao);

        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setId(123);

        service.createTrainee(trainee);

        Trainee fetched = dao.select(123);
        assertNotNull(fetched);
        assertEquals("John.Doe", fetched.getUserName());
    }
}
