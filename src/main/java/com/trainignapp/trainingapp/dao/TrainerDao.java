package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerDao {
    Map<Integer, Trainer> trainerStroage = new HashMap<>();

    public void create(Trainer trainer) {
        trainerStroage.put(trainer.getId(), trainer);
    }


    public void update(Trainer trainer) {
        trainerStroage.replace(trainer.getId(), trainer);
    }

    public Trainer select(int id) {
        return trainerStroage.get(id);
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerStroage.values();
    }
}
