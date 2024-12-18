package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void select_ShouldReturnNullWhenTrainerDoesNotExist() {
        String username = "Alice.Smith";

        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        Trainer result = trainerService.select(username);

        assertNull(result);
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

        Trainer oldTrainer = new Trainer();
        oldTrainer.setUsername(username);
        oldTrainer.setPassword("securePassword");

        Trainer newTrainer = new Trainer();
        newTrainer.setUsername("Alice.Smith");
        newTrainer.setFirstName("Alice");
        newTrainer.setLastName("Smith");
        newTrainer.setPassword("securePassword");
        newTrainer.setIsActive(true);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(oldTrainer));

        trainerService.updateProfile(username, newTrainer);

        verify(trainerDao).save(oldTrainer);
        assertEquals("Alice", oldTrainer.getFirstName());
        assertEquals("Smith", oldTrainer.getLastName());
    }

    @Test
    void deactivateTrainer_ShouldToggleIsActive() {
        String username = "Alice.Smith";

        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setPassword("securePassword");
        trainer.setIsActive(true);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainer(username);

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
