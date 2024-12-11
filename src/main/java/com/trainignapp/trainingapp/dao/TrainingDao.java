package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class TrainingDao {
    Map<String, Training> trainingStorage = new HashMap<>();

    public void create(Training training) {
        trainingStorage.put(training.getName(), training);
    }

    public Training select(String trainingName) {
        return trainingStorage.get(trainingName);
    }
}
