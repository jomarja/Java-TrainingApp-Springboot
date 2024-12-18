package com.trainignapp.trainingapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trainingTypeName;
}
