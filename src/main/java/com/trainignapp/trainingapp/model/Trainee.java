package com.trainignapp.trainingapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Trainee extends User {
    @Column
    private String address;

    @Column
    private Date dateOfBirth;
}
