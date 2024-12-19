package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTraineeTrainingsByCriteria_ShouldReturnFilteredTrainings() {
        String traineeUsername = "john.doe";
        Date fromDate = new Date();
        Date toDate = new Date();
        String trainerName = "alice.smith";
        String trainingType = "Math";

        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        Trainer trainer = new Trainer();
        trainer.setUsername(trainerName);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName(trainingType);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);

        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainingDao.findByDate(traineeUsername, fromDate, toDate)).thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainingsByCriteria(traineeUsername, fromDate, toDate, trainerName, trainingType);

        assertEquals(1, result.size());
        assertEquals(trainerName, result.get(0).getTrainer().getUsername());
        assertEquals(trainingType, result.get(0).getTrainingType().getTrainingTypeName());
    }

    @Test
    void addTraining_ShouldSaveNewTraining() {
        String traineeUsername = "john.doe";
        String trainerUsername = "alice.smith";
        String trainingTypeName = "Math";

        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        Trainer trainer = new Trainer();
        trainer.setUsername(trainerUsername);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName(trainingTypeName);

        Training training = new Training();

        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName(trainingTypeName)).thenReturn(Optional.of(type));

        trainingService.addTraining(traineeUsername, trainerUsername, trainingTypeName, training);

        verify(trainingDao).save(training);
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(type, training.getTrainingType());
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnUnassignedTrainers() {
        String traineeUsername = "john.doe";

        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        Trainer assignedTrainer = new Trainer();
        assignedTrainer.setUsername("alice.smith");

        Trainer unassignedTrainer = new Trainer();
        unassignedTrainer.setUsername("bob.jones");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(assignedTrainer);

        when(trainerDao.findAll()).thenReturn(List.of(assignedTrainer, unassignedTrainer));
        when(trainingDao.findByUsernames(assignedTrainer.getUsername(), traineeUsername)).thenReturn(List.of(training));
        when(trainingDao.findByUsernames(unassignedTrainer.getUsername(), traineeUsername)).thenReturn(List.of());

        List<Trainer> result = trainingService.getUnassignedTrainersForTrainee(traineeUsername);

        assertEquals(1, result.size());
        assertEquals("bob.jones", result.get(0).getUsername());
    }

    @Test
    void updateTraineeTrainers_ShouldUpdateAndSaveNewTrainings() {
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = List.of("alice.smith", "bob.jones");

        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("alice.smith");

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("bob.jones");

        Training existingTraining = new Training();
        existingTraining.setTrainee(trainee);

        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerDao.findAll()).thenReturn(List.of(trainer1, trainer2));
        when(trainingDao.findByTraineeUsername(traineeUsername)).thenReturn(List.of(existingTraining));

        trainingService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        verify(trainingDao).deleteAll(List.of(existingTraining));
        ArgumentCaptor<List<Training>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainingDao).saveAll(captor.capture());

        List<Training> savedTrainings = captor.getValue();
        assertEquals(2, savedTrainings.size());
        assertEquals(trainer1, savedTrainings.get(0).getTrainer());
        assertEquals(trainer2, savedTrainings.get(1).getTrainer());
    }
}
