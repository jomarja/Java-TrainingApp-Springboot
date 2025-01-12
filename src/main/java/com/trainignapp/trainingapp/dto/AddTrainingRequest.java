package com.trainignapp.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AddTrainingRequest {
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
    @NotBlank(message = "Training name is required")
    private String trainingName;
    @NotNull(message = "Training date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date trainingDate;
    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;
    @NotBlank(message = "Training type is required")
    private String trainingType;
}