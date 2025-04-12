package com.trainingapp.microservice.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthRecord {
    private int month;
    private int trainingSummaryDuration;
}
