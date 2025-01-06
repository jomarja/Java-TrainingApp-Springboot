package com.trainignapp.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainingTypeResponse {
    private Long id;
    private String trainingTypeName;
}
