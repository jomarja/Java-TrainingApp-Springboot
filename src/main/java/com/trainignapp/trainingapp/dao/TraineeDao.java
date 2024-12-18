package com.trainignapp.trainingapp.dao;

import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.repository.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TraineeDao {

    @Autowired
    private TraineeRepository repository;


    public void save(Trainee trainee) {
        repository.save(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public void delete(Trainee trainee) {
        repository.delete(trainee);
    }

    public List<Trainee> findAll() {
        return repository.findAll();
    }
}
