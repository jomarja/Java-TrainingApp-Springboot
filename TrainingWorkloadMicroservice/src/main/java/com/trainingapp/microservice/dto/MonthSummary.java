package com.trainingapp.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthSummary {
    private int month;
    private int trainingSummaryDuration;
}
