package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dao.TrainingTypeDao;
import com.trainignapp.trainingapp.dto.TraineeTrainingResponse;
import com.trainignapp.trainingapp.dto.TrainerDetails;
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
        Date fromDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Date toDate = new Date();
        String trainerName = "alice.smith";
        String trainingType = "Math";

        // Mock Trainee
        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        // Mock Trainer
        Trainer trainer = new Trainer();
        trainer.setUsername(trainerName);

        // Mock TrainingType
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(trainingType);

        // Mock Training
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);
        training.setTrainingName("Algebra Basics");
        training.setTrainingDate(new Date());
        training.setTrainingDuration(120);

        // Mock DAO behavior
        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainingDao.findByTraineeDate(traineeUsername, fromDate, toDate)).thenReturn(List.of(training));

        // Call the method
        List<TraineeTrainingResponse> result = trainingService.getTraineeTrainingsByCriteria(traineeUsername, fromDate, toDate, trainerName, trainingType);

        // Assertions
        assertEquals(1, result.size());
        TraineeTrainingResponse response = result.get(0);
        assertEquals("Algebra Basics", response.getTrainingName());
        assertEquals(trainingType, response.getTrainingType());
        assertEquals(trainerName, response.getTrainerName());
        assertEquals(120, response.getTrainingDuration());
    }

    @Test
    void addTraining_ShouldSaveNewTraining() {
        String traineeUsername = "john.doe";
        String trainerUsername = "alice.smith";
        String trainingTypeName = "Math";
        String trainingName = "Advanced Math";
        Date trainingDate = new Date();
        Integer trainingDuration = 90;

        // Mocked entities
        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        Trainer trainer = new Trainer();
        trainer.setUsername(trainerUsername);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName(trainingTypeName);

        // Mock DAO behavior
        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName(trainingTypeName)).thenReturn(Optional.of(type));

        // Call the service method
        trainingService.addTraining(traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration, trainingTypeName);

        // Capture the Training object passed to save
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());
        Training savedTraining = captor.getValue();

        // Assertions
        assertEquals(trainee, savedTraining.getTrainee());
        assertEquals(trainer, savedTraining.getTrainer());
        assertEquals(type, savedTraining.getTrainingType());
        assertEquals(trainingName, savedTraining.getTrainingName());
        assertEquals(trainingDate, savedTraining.getTrainingDate());
        assertEquals(trainingDuration, savedTraining.getTrainingDuration());
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

        // Create mock Trainee
        Trainee trainee = new Trainee();
        trainee.setUsername(traineeUsername);

        // Create mock TrainingType
        TrainingType fitness = new TrainingType();
        fitness.setId(1L);
        fitness.setTrainingTypeName("Fitness");

        // Create mock Trainers
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("alice.smith");
        trainer1.setSpecialization(fitness); // Set specialization

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("bob.jones");
        trainer2.setSpecialization(fitness); // Set specialization

        // Create existing training
        Training existingTraining = new Training();
        existingTraining.setTrainee(trainee);

        // Mock DAO calls
        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerDao.findAll()).thenReturn(List.of(trainer1, trainer2));
        when(trainingDao.findByTraineeUsername(traineeUsername)).thenReturn(List.of(existingTraining));

        // Call the method
        List<TrainerDetails> result = trainingService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        // Verify DAO interactions
        verify(trainingDao).deleteAll(List.of(existingTraining));
        ArgumentCaptor<List<Training>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainingDao).saveAll(captor.capture());

        // Assert new trainings were saved
        List<Training> savedTrainings = captor.getValue();
        assertEquals(2, savedTrainings.size());
        assertEquals(trainer1, savedTrainings.get(0).getTrainer());
        assertEquals(trainer2, savedTrainings.get(1).getTrainer());

        // Assert response
        assertEquals(2, result.size());
        assertEquals("alice.smith", result.get(0).getUsername());
        assertEquals("bob.jones", result.get(1).getUsername());
    }
}
