package com.trainignapp.trainingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "specialization_id", referencedColumnName = "trainingTypeName", nullable = false)
    private TrainingType specialization;
}
