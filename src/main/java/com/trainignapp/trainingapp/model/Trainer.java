package com.trainignapp.trainingapp.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "specialization_id", referencedColumnName = "trainingTypeName", nullable = false)
    private TrainingType specialization;
}
