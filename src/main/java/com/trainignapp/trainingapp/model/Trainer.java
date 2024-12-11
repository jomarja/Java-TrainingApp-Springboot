package com.trainignapp.trainingapp.model;

public class Trainer extends User {

    private String specialization;
    private int userId;

    public Trainer(int id, String firstName, String lastName, String userName, String password, Boolean isActive, String specialization, int userId) {
        super(id, firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
        this.userId = userId;
    }
    public Trainer(){
        super();
    }

    public Trainer(int id, String firstName, String lastName, String userName, String password, Boolean isActive) {
        super(id, firstName, lastName, userName, password, isActive);
    }


    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
