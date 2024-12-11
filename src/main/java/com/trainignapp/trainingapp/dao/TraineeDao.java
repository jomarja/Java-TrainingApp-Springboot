package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TraineeDao {
    private Map<Integer, Trainee> traineeStorage = new HashMap<>();

    public void create(Trainee trainee) {
        traineeStorage.put(trainee.getId(), trainee);
    }

    public void update(Trainee trainee) {
        traineeStorage.replace(trainee.getId(), trainee);
    }

    public void delete(int id) {
        traineeStorage.remove(id);
    }

    public Trainee select(int id) {
        return traineeStorage.get(id);
    }

    public Collection<Trainee> getAllTrainees() {
        return traineeStorage.values();
    }
}
