package com.trainignapp.trainingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainerRegistrationRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "specialization is required")
    private String specialization;
}
