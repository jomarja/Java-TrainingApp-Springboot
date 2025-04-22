package com.trainignapp.trainingapp.cucumber.steps;

import com.trainignapp.trainingapp.dao.TraineeDao;
import com.trainignapp.trainingapp.dao.TrainerDao;
import com.trainignapp.trainingapp.dao.TrainingDao;
import com.trainignapp.trainingapp.dto.TraineeTrainingResponse;
import com.trainignapp.trainingapp.dto.TrainerTrainingResponse;
import com.trainignapp.trainingapp.model.Trainee;
import com.trainignapp.trainingapp.model.Trainer;
import com.trainignapp.trainingapp.model.Training;
import com.trainignapp.trainingapp.model.TrainingType;
import com.trainignapp.trainingapp.service.TrainingService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ScenarioScope
public class MainServiceSteps {
    private final TrainingService service;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    private List<TraineeTrainingResponse> traineeResults;
    private List<TrainerTrainingResponse> trainerResults;

    public MainServiceSteps(TrainingService service, TraineeDao traineeDao, TrainerDao trainerDao, TrainingDao trainingDao) {
        this.service = service;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
    }

    @Given("a trainee {string} exists")
    public void a_trainee_exists(String username) {
        Trainee mockT = new Trainee();
        mockT.setUsername(username);
        mockT.setFirstName("First");
        mockT.setLastName("Last");
        mockT.setPassword("pw");
        mockT.setIsActive(true);
        // leave address/dateOfBirth null
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(mockT));
    }

    @Given("the following trainings exist for trainee {string}:")
    public void the_following_trainings_exist_for_trainee(String username, DataTable table) {
        List<Training> list = table.asMaps().stream().map(row -> {
            Training t = new Training();
            t.setTrainingName(row.get("trainingName"));
            t.setTrainingDate(toDate(row.get("trainingDate")));
            t.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
            Trainer tr = new Trainer();
            tr.setUsername(row.get("trainerUsername"));
            t.setTrainer(tr);
            TrainingType tt = new TrainingType();
            tt.setTrainingTypeName(row.get("trainingTypeName"));
            t.setTrainingType(tt);
            return t;
        }).toList();

        when(trainingDao.findByTraineeDate(eq(username), any(), any())).thenReturn(list);
    }

    @When("I request trainee trainings for {string} from {string} to {string}")
    public void i_request_trainee_trainings(String username, String from, String to) {
        traineeResults = service.getTraineeTrainingsByCriteria(username, toDate(from), toDate(to), null, null);
    }

    @When("I request trainee trainings for {string} from {string} to {string} filtered by trainer {string}")
    public void i_request_trainee_trainings_filtered(String username, String from, String to, String trainerName) {
        traineeResults = service.getTraineeTrainingsByCriteria(username, toDate(from), toDate(to), trainerName, null);
    }

    @Then("I should get {int} trainings")
    public void i_should_get_count_trainings(int count) {
        assertEquals(count, traineeResults.size());
    }

    @Given("a trainer {string} exists")
    public void a_trainer_exists(String username) {
        Trainer mockR = new Trainer();
        mockR.setUsername(username);
        mockR.setFirstName("First");
        mockR.setLastName("Last");
        mockR.setPassword("pw");
        mockR.setIsActive(true);
        TrainingType spec = new TrainingType();
        spec.setTrainingTypeName("ANY");
        mockR.setSpecialization(spec);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(mockR));
    }

    @Given("the following trainings exist for trainer {string}:")
    public void the_following_trainings_exist_for_trainer(String username, DataTable table) {
        List<Training> list = table.asMaps().stream().map(row -> {
            Training t = new Training();
            t.setTrainingName(row.get("trainingName"));
            t.setTrainingDate(toDate(row.get("trainingDate")));
            t.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
            Trainee te = new Trainee();
            te.setUsername(row.get("traineeUsername"));
            t.setTrainee(te);
            TrainingType tt = new TrainingType();
            tt.setTrainingTypeName(row.get("trainingTypeName"));
            t.setTrainingType(tt);
            return t;
        }).toList();

        when(trainingDao.findByTrainerDate(eq(username), any(), any())).thenReturn(list);
    }

    @When("I request trainer trainings for {string} from {string} to {string}")
    public void i_request_trainer_trainings(String username, String from, String to) {
        trainerResults = service.getTrainerTrainingsByCriteria(username, toDate(from), toDate(to), null);
    }

    @Then("I should get {int} trainer trainings")
    public void i_should_get_count_trainer_trainings(int count) {
        assertEquals(count, trainerResults.size());
    }

    // helper
    private Date toDate(String s) {
        try {
            return new SimpleDateFormat("yyyy‑MM‑dd").parse(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
