package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.service.TraineeService;
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

class TraineeServiceTest {
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
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

        // Mock the MeterRegistry to return the mock Counter
        when(meterRegistry.counter("trainee.created.count")).thenReturn(mockCounter);

        // Manually initialize TraineeService with mocked dependencies
        traineeService = new TraineeService(traineeDao, trainerDao, trainingDao, meterRegistry);
    }

    @Test
    void createTrainee_ShouldGenerateUsernameAndPasswordAndSave() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findAll()).thenReturn(List.of());

        traineeService.createTrainee(trainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).save(captor.capture());

        Trainee savedTrainee = captor.getValue();
        assertNotNull(savedTrainee.getUsername());
        assertNotNull(savedTrainee.getPassword());
        assertTrue(savedTrainee.getUsername().startsWith("John.Doe"));

        verify(mockCounter).increment();
    }

    @Test
    void authenticateTrainee_ShouldReturnTrueWhenPasswordMatches() {
        String username = "John.Doe";
        String password = "securePassword";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword(password);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        boolean isAuthenticated = traineeService.authenticateTrainee(username, password);

        assertTrue(isAuthenticated);
    }

    @Test
    void authenticateTrainee_ShouldReturnFalseWhenPasswordDoesNotMatch() {
        String username = "John.Doe";
        String password = "wrongPassword";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword("securePassword");

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        boolean isAuthenticated = traineeService.authenticateTrainee(username, password);

        assertFalse(isAuthenticated);
    }

    @Test
    void shouldNotRegisterTraineeIfAlreadyTrainer() {
        String username = "John.Doe";

        // Ensure that the unique username generation aligns with the mock expectations
        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(new Trainer()));
        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());

        // Create a Trainee object with firstName and lastName that lead to the mocked username
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        // Mock the DAO call if it is not mocked already
        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());

        // Assert that the exception is thrown when trying to create a trainee who is already a trainer
        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(trainee));
    }

    @Test
    void select_ShouldReturnTraineeWhenExists() {
        String username = "John.Doe";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.select(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void updateTraineePassword_ShouldUpdatePasswordWhenAuthenticated() {
        String username = "John.Doe";
        String oldPassword = "securePassword";
        String newPassword = "newSecurePassword";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword(oldPassword);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.updateTraineePassword(username, newPassword);

        verify(traineeDao).save(trainee);
        assertEquals(newPassword, trainee.getPassword());
    }

    @Test
    void deactivateTrainee_ShouldToggleIsActive() {
        String username = "John.Doe";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword("securePassword");
        trainee.setIsActive(true);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deactivateTrainee(username, false);

        verify(traineeDao).save(trainee);
        assertFalse(trainee.getIsActive());
    }

    @Test
    void deleteTrainee_ShouldDeleteWhenAuthenticated() {
        String username = "John.Doe";
        String password = "securePassword";

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword(password);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(username);

        verify(traineeDao).delete(trainee);
    }

    @Test
    void generateRandomPassword_ShouldReturnNonEmptyPassword() {
        String password = traineeService.generateRandomPassword();

        assertNotNull(password);
        assertFalse(password.isEmpty());
        assertEquals(10, password.length());
    }
}
