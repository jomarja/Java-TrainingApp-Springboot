package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTraineeResponse {
    private String username;
    private String firstName;
    private String lastName;
}
