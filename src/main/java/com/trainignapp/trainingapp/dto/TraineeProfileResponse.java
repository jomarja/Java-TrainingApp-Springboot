package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeProfileResponse {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<TraineeTrainerResponse> trainers;
}
