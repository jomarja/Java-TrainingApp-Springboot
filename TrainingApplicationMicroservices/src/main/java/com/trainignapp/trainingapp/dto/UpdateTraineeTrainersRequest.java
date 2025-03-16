package com.trainignapp.trainingapp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTraineeTrainersRequest {
    @NotEmpty(message = "Trainers list cannot be empty")
    private List<String> trainerUsernames;
}