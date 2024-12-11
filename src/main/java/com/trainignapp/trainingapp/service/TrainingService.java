package com.trainignapp.trainingapp.service;

import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private TrainingDao trainingDao;

    @Autowired
    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }


    public void createTraining(Training training) {
        trainingDao.create(training);
        logger.info("Creating a new training: {}", training.getName());
    }

    public Training select(String name) {
        return this.trainingDao.select(name);
    }
}
