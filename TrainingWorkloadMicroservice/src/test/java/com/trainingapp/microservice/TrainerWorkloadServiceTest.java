package com.trainingapp.microservice;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.dto.TrainerWorkloadSummary;
import com.trainingapp.microservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerWorkloadServiceTest {
    @Test
    public void testUpdateWorkloadAddAndMonthlySummary() {
        TrainerWorkloadService service = new TrainerWorkloadService();

        // Create a training request for January 2023 with 60 minutes duration.
        TrainerWorkloadRequest addRequest = new TrainerWorkloadRequest();
        addRequest.setTrainerUsername("trainer1");
        addRequest.setTrainerFirstName("John");
        addRequest.setTrainerLastName("Doe");
        addRequest.setTrainingDate(new GregorianCalendar(2023, Calendar.JANUARY, 10).getTime());
        addRequest.setActionType("ADD");
        addRequest.setTrainingDuration(60);
        addRequest.setActive(true);

        service.updateWorkload(addRequest);

        TrainerWorkloadSummary summary = service.getMonthlySummary("trainer1", 2023);
        // Verify that January (month 1) workload is 60 minutes.
        assertNotNull(summary.getMonthlyWorkload().get(1), "Expected January workload to be present");
        assertEquals(60, summary.getMonthlyWorkload().get(1).intValue());
        assertEquals("John", summary.getTrainerFirstName());
        assertEquals("Doe", summary.getTrainerLastName());
        assertTrue(summary.isActive());
    }

    @Test
    public void testUpdateWorkloadDelete() {
        TrainerWorkloadService service = new TrainerWorkloadService();

        // First add a training session in February 2023.
        TrainerWorkloadRequest addRequest = new TrainerWorkloadRequest();
        addRequest.setTrainerUsername("trainer1");
        addRequest.setTrainerFirstName("John");
        addRequest.setTrainerLastName("Doe");
        addRequest.setTrainingDate(new GregorianCalendar(2023, Calendar.FEBRUARY, 15).getTime());
        addRequest.setActionType("ADD");
        addRequest.setTrainingDuration(90);
        addRequest.setActive(true);
        service.updateWorkload(addRequest);

        // Now simulate deletion of 30 minutes.
        TrainerWorkloadRequest deleteRequest = new TrainerWorkloadRequest();
        deleteRequest.setTrainerUsername("trainer1");
        deleteRequest.setTrainerFirstName("John");
        deleteRequest.setTrainerLastName("Doe");
        deleteRequest.setTrainingDate(new GregorianCalendar(2023, Calendar.FEBRUARY, 15).getTime());
        deleteRequest.setActionType("DELETE");
        deleteRequest.setTrainingDuration(30);
        deleteRequest.setActive(true);
        service.updateWorkload(deleteRequest);

        TrainerWorkloadSummary summary = service.getMonthlySummary("trainer1", 2023);
        // Expect February (month 2) workload to be reduced to 60 minutes.
        assertNotNull(summary.getMonthlyWorkload().get(2), "Expected February workload to be present");
        assertEquals(60, summary.getMonthlyWorkload().get(2).intValue());
    }
}
