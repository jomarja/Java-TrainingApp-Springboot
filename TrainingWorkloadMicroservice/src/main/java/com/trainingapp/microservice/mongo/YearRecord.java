package com.trainingapp.microservice.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearRecord {
    private int year;
    private List<MonthRecord> months;
}
