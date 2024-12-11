package com.trainignapp.trainingapp.model;


import java.time.LocalDate;


public class Training {
    private int trainee_id;
    private int trainer_id;
    private String name;
    private String type;
    private LocalDate training_date;
    private int dutration;


    public Training(int trainee_id, int trainer_id, String name, int dutration) {
        this.trainee_id = trainee_id;
        this.trainer_id = trainer_id;
        this.name = name;
        this.dutration = dutration;
    }

    public Training(int trainee_id, int trainer_id, String name, String type, LocalDate training_date, int dutration) {
        this.trainee_id = trainee_id;
        this.trainer_id = trainer_id;
        this.name = name;
        this.type = type;
        this.dutration = dutration;
    }

    public Training() {

    }

    public int getTrainee_id() {
        return trainee_id;
    }

    public void setTrainee_id(int trainee_id) {
        this.trainee_id = trainee_id;
    }

    public int getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(int trainer_id) {
        this.trainer_id = trainer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getTraining_date() {
        return training_date;
    }

    public void setTraining_date(LocalDate training_date) {
        this.training_date = training_date;
    }

    public int getDutration() {
        return dutration;
    }

    public void setDutration(int dutration) {
        this.dutration = dutration;
    }


}
