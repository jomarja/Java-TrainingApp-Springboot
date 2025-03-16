package com.trainignapp.trainingapp.dto;

public class MonthSummary {
    private int month;
    private int trainingSummaryDuration;

    public MonthSummary() {
    }

    public MonthSummary(int month, int trainingSummaryDuration) {
        this.month = month;
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainingSummaryDuration(int trainingSummaryDuration) {
        this.trainingSummaryDuration = trainingSummaryDuration;
    }
}
