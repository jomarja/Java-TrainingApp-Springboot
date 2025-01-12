package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TrainerTrainingResponse {
    private String trainingName;
    private Date trainingDate;
    private String trainingType;
    private Integer trainingDuration;
    private String traineeName;
}
