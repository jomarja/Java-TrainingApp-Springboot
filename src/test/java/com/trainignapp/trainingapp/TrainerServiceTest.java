package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dto.TrainerProfileResponseFull;
import com.trainignapp.trainingapp.dto.UpdateTrainerProfileRequest;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.service.TraineeService;
import com.trainignapp.trainingapp.service.TrainerService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerServiceTest {
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeDao traineeDao;
    @InjectMocks
    private TrainerService trainerService;
    @InjectMocks
    private TraineeService traineeService;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter mockCounter;
    @Mock
    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock MeterRegistry to return the mocked Counter
        when(meterRegistry.counter("trainer.created.count")).thenReturn(mockCounter);

        // Manually initialize TrainerService with mocked dependencies
        trainerService = new TrainerService(trainerDao, traineeDao, trainingDao, meterRegistry);
    }

    @Test
    void createTrainer_ShouldGenerateUsernameAndPasswordAndSave() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");

        when(trainerDao.findAll()).thenReturn(List.of());

        trainerService.createTrainer(trainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        Trainer savedTrainer = captor.getValue();
        assertNotNull(savedTrainer.getUsername());
        assertNotNull(savedTrainer.getPassword());
        assertTrue(savedTrainer.getUsername().startsWith("Alice.Smith"));

        verify(mockCounter).increment();
    }

    @Test
    void shouldNotRegisterTrainerIfAlreadyTrainee() {
        String username = "John.Doe";

        // Ensure that the unique username generation aligns with the mock expectations
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        // Create a Trainee object with firstName and lastName that lead to the mocked username
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        // Mock the DAO call if it is not mocked already
        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        // Assert that the exception is thrown when trying to create a trainee who is already a trainer
        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(trainer));
    }

    @Test
    void authenticateTrainer_ShouldReturnTrueWhenPasswordMatches() {
        String username = "Alice.Smith";
        String password = "securePassword";

        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setPassword(password);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        boolean isAuthenticated = trainerService.authenticateTrainer(username, password);

        assertTrue(isAuthenticated);
    }

    @Test
    void authenticateTrainer_ShouldThrowExceptionWhenUsernameNotFound() {
        String username = "Alice.Smith";
        String password = "securePassword";

        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            trainerService.authenticateTrainer(username, password);
        });

        assertEquals("Wrong username", exception.getMessage());
    }

    @Test
    void select_ShouldReturnTrainerWhenExists() {
        String username = "Alice.Smith";

        Trainer trainer = new Trainer();
        trainer.setUsername(username);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.select(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void updateTrainerPassword_ShouldUpdatePasswordWhenAuthenticated() {
        String username = "Alice.Smith";
        String oldPassword = "securePassword";
        String newPassword = "newSecurePassword";

        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setPassword(oldPassword);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.updateTrainerPassword(username, newPassword);

        verify(trainerDao).save(trainer);
        assertEquals(newPassword, trainer.getPassword());
    }

    @Test
    void updateProfile_ShouldUpdateTrainerProfileWhenAuthenticated() {
        String username = "Alice.Smith";

        // Existing trainer
        Trainer oldTrainer = new Trainer();
        oldTrainer.setUsername(username);
        oldTrainer.setPassword("securePassword");
        oldTrainer.setIsActive(true);

        // Update request
        UpdateTrainerProfileRequest newTrainer = new UpdateTrainerProfileRequest();
        newTrainer.setFirstName("Alice");
        newTrainer.setLastName("Smith");
        newTrainer.setIsActive(true);

        // Mock specialization
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
        oldTrainer.setSpecialization(trainingType);

        // Mock training
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Training training = new Training();
        training.setTrainee(trainee);

        // Mock DAO responses
        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(oldTrainer));
        when(trainingDao.findByTrainerUsername(username)).thenReturn(List.of(training));

        // Call the method
        TrainerProfileResponseFull response = trainerService.updateProfile(username, newTrainer);

        // Verify updates
        verify(trainerDao).save(oldTrainer);
        assertEquals("Alice", oldTrainer.getFirstName());
        assertEquals("Smith", oldTrainer.getLastName());
        assertEquals("Fitness", response.getSpecialization());
        assertEquals(1, response.getTrainees().size());
        assertEquals("trainee1", response.getTrainees().get(0).getUsername());
    }

    @Test
    void deactivateTrainer_ShouldToggleIsActive() {
        String username = "Alice.Smith";

        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setPassword("securePassword");
        trainer.setIsActive(true);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainer(username, false);

        verify(trainerDao).save(trainer);
        assertFalse(trainer.getIsActive());
    }

    @Test
    void generateRandomPassword_ShouldReturnNonEmptyPassword() {
        String password = trainerService.generateRandomPassword();

        assertNotNull(password);
        assertFalse(password.isEmpty());
        assertEquals(10, password.length());
    }
}
