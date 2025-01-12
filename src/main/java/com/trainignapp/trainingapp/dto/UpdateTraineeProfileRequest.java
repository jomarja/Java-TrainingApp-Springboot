package com.trainignapp.trainingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateTraineeProfileRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private Date dateOfBirth;
    private String address;
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
