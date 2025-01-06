package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainerDao {
    private final TrainerRepository repository;

    @Autowired
    public TrainerDao(TrainerRepository repository) {
        this.repository = repository;
    }

    public void save(Trainer trainer) {
        repository.save(trainer);
    }

    public Optional<Trainer> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<Trainer> findAll() {
        return repository.findAll();
    }
}
