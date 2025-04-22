package com.trainingapp.microservice.cucumber.steps;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.dto.TrainerWorkloadSummary;
import com.trainingapp.microservice.repository.TrainerTrainingSummaryRepository;
import com.trainingapp.microservice.service.TrainerMongoService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ScenarioScope
public class TrainerWorkloadSteps {
    private final TrainerMongoService service;
    private final TrainerTrainingSummaryRepository repo;
    private TrainerWorkloadSummary result;

    public TrainerWorkloadSteps(TrainerMongoService service, TrainerTrainingSummaryRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @Given("the MongoDB is clean")
    public void cleanDatabase() {
        repo.deleteAll();
    }

    @Given("an existing summary for {string} in {string} with duration {int}")
    public void seedSummary(String username, String yearMonth, Integer duration) throws Exception {
        Date trainingDate = new SimpleDateFormat("yyyy-MM-dd").parse(yearMonth + "-01");
        TrainerWorkloadRequest seed = new TrainerWorkloadRequest();
        seed.setTrainerUsername(username);
        seed.setTrainerFirstName("seed");
        seed.setTrainerLastName("data");
        seed.setActive(true);
        seed.setTrainingDate(trainingDate);
        seed.setTrainingDuration(duration);
        seed.setActionType("ADD");
        service.updateTrainerSummary(seed, "seedTx");
    }

    @When("I send a workload request with")
    public void sendWorkload(DataTable table) throws Exception {
        Map<String, String> row = table.asMaps().get(0);
        TrainerWorkloadRequest req = new TrainerWorkloadRequest();
        req.setTrainerUsername(row.get("trainerUsername"));
        req.setTrainerFirstName(row.get("firstName"));
        req.setTrainerLastName(row.get("lastName"));
        req.setActive(Boolean.parseBoolean(row.get("active")));
        req.setTrainingDate(new SimpleDateFormat("yyyy-MM-dd").parse(row.get("trainingDate")));
        req.setTrainingDuration(Integer.parseInt(row.get("duration")));
        req.setActionType(row.get("action"));
        service.updateTrainerSummary(req, "testTx");
    }

    @When("I request monthly summary for trainer {string} year {int}")
    public void requestSummary(String username, int year) {
        result = service.getMonthlySummary(username, year);
    }

    @Then("the summary for {string} in year {int} month {int} should have total duration {int}")
    public void verifyDuration(String username, int year, int month, int expected) {
        Map<Integer, Integer> map = result.getMonthlyWorkload();
        assertTrue(map.containsKey(month));
        assertEquals(expected, map.get(month));
    }

    @Then("I get an empty workload map")
    public void emptyMap() {
        assertTrue(result.getMonthlyWorkload().isEmpty());
    }
}
