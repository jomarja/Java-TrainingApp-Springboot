package com.trainignapp.trainingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class TraineeRegistrationRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private Date dateOfBirth; // Optional, stored as a String for simplicity
    private String address;     // Optional
}
