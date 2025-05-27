package com.eurika.server.trainingapp.service;

import com.eurika.server.trainingapp.dao.TraineeDao;
import com.eurika.server.trainingapp.dao.TrainerDao;
import com.eurika.server.trainingapp.model.Trainee;
import com.eurika.server.trainingapp.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    @Autowired
    public CustomUserDetailsService(TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Trainer> trainerOpt = trainerDao.findByUsername(username);
        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();
            return new User(trainer.getUsername(), trainer.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINER")));
        }

        Optional<Trainee> traineeOpt = traineeDao.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            return new User(trainee.getUsername(), trainee.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
