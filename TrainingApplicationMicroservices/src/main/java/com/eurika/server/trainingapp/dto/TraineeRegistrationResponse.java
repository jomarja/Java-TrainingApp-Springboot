package com.eurika.server.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeRegistrationResponse {
    private String username;
    private String password;
}
