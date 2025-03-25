package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeRegistrationResponse {
    private String username;
    private String password;
}
