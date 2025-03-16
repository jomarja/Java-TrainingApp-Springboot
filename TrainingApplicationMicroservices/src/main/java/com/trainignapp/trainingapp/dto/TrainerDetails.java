package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerDetails {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}
