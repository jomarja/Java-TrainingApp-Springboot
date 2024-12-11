package com.trainignapp.trainingapp.model;


import java.time.LocalDate;


public class Trainee extends User {

    private LocalDate date_of_birth;
    private String address;
    private int UserId;

    public Trainee(){

    }

    public Trainee(String firstName, String lastName, String userName, String password, Boolean isActive, LocalDate date_of_birth, String address, int userId) {
        super(firstName, lastName, userName, password, isActive);
        this.date_of_birth = date_of_birth;
        this.address = address;
        UserId = userId;
    }

    public Trainee(String firstName, String lastName, String userName, String password, Boolean isActive, String address, int userId) {
        super(firstName, lastName, userName, password, isActive);
        this.address = address;
        UserId = userId;
    }


    public Trainee(String firstName, String lastName, String userName, String password, Boolean isActive) {
        super(firstName, lastName, userName, password, isActive);
    }


    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }


}
