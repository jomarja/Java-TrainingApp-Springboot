package com.trainignapp.trainingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "specialization_id", referencedColumnName = "trainingTypeName", nullable = false)
    private TrainingType specialization;
}
