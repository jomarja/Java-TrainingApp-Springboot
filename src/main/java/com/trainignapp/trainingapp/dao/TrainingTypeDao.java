package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.dto.TrainingTypeResponse;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.repository.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainingTypeDao {
    private final TrainingTypeRepository repository;

    @Autowired
    public TrainingTypeDao(TrainingTypeRepository repository) {
        this.repository = repository;
    }

    public Optional<TrainingType> findByName(String trainingTypeName) {
        return repository.findByTrainingTypeName(trainingTypeName);
    }

    public List<TrainingTypeResponse> findAll() {
        return repository.findAll().stream().map(trainingType -> new TrainingTypeResponse(trainingType.getId(), trainingType.getTrainingTypeName())).toList();
    }
}
