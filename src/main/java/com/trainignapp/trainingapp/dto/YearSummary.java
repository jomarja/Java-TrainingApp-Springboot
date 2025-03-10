package com.trainignapp.trainingapp.dto;

import java.util.List;

public class YearSummary {
    private int year;
    private List<MonthSummary> months;

    public YearSummary() {
    }

    public YearSummary(int year, List<MonthSummary> months) {
        this.year = year;
        this.months = months;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<MonthSummary> getMonths() {
        return months;
    }

    public void setMonths(List<MonthSummary> months) {
        this.months = months;
    }
}
