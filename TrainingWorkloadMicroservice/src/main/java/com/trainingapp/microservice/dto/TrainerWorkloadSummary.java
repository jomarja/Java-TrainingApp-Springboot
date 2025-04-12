package com.trainingapp.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadSummary {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean isActive;
    private Map<Integer, Integer> monthlyWorkload = new HashMap<>();

    public TrainerWorkloadSummary(String trainerUsername, String trainerFirstName, String trainerLastName, boolean isActive) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.isActive = isActive;
    }
}
