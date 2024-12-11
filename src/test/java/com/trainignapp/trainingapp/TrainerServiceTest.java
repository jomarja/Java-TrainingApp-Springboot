package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.service.TrainerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainerServiceTest {
    @Test
    public void testCreateTrainer() {
        TrainerDao dao = new TrainerDao();
        TrainerService service = new TrainerService(dao);

        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setId(123);

        service.createTrainer(trainer);

        Trainer fetched = dao.select(123);
        assertNotNull(fetched);
        assertEquals("John.Doe", fetched.getUserName());
    }
}
