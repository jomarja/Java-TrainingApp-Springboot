package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeTrainerResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization; // Training type reference
}
