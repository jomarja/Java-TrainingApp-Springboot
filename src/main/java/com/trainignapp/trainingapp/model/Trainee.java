package com.trainignapp.trainingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Trainee extends User {
    @Column
    private String address;

    @Column
    private Date dateOfBirth;
}
