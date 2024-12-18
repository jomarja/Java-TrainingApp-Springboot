package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.repository.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrainingTypeDao {
    @Autowired
    private TrainingTypeRepository repository;

    public Optional<TrainingType> findByName(String trainingTypeName) {
        return repository.findByTrainingTypeName(trainingTypeName);
    }

}
