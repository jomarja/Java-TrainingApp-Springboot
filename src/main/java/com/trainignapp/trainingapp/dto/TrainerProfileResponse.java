package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerProfileResponse {
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    List<TrainerTraineeResponse> trainees;
}
