package com.trainignapp.trainingapp.Storage;

import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class StorageBean {


    MapsImplementation mapsImplementation = new MapsImplementation();

    @PostConstruct
    public void init() throws IOException {
       mapsImplementation.MapsImpl();
    }

    public MapsImplementation getMapsImplementation() {
        return mapsImplementation;
    }

    public Map<Integer, Trainee> getTrainees() {
        return getMapsImplementation().getTrainees();
    }

    public Map<Integer, Trainer> getTrainers() {
        return getMapsImplementation().getTrainers();
    }

    public Map<String, Training> getTrainings() {
        return getMapsImplementation().getTrainings();
    }
}
