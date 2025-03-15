package com.trainingapp.microservice.service;

import com.trainingapp.microservice.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerWorkloadService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadService.class);
    private final Map<String, TrainerWorkloadSummary> workloadDB = new ConcurrentHashMap<>();

    public void updateWorkload(TrainerWorkloadRequest request) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(request.getTrainingDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String key = request.getTrainerUsername() + "_" + year;
        TrainerWorkloadSummary summary = workloadDB.get(key);
        if (summary == null) {
            summary = new TrainerWorkloadSummary(request.getTrainerUsername(), request.getTrainerFirstName(), request.getTrainerLastName(), request.isActive());
        } else {
            summary.setTrainerFirstName(request.getTrainerFirstName());
            summary.setTrainerLastName(request.getTrainerLastName());
            summary.setActive(request.isActive());
        }
        int currentDuration = summary.getMonthlyWorkload().getOrDefault(month, 0);
        if ("ADD".equalsIgnoreCase(request.getActionType())) {
            summary.getMonthlyWorkload().put(month, currentDuration + request.getTrainingDuration());
        } else if ("DELETE".equalsIgnoreCase(request.getActionType())) {
            int updatedDuration = Math.max(0, currentDuration - request.getTrainingDuration());
            summary.getMonthlyWorkload().put(month, updatedDuration);
        }
        workloadDB.put(key, summary);
        logger.info("Updated workloadDB key {} with summary {}", key, summary);
    }

    public TrainerWorkloadSummary getMonthlySummary(String trainerUsername, int year) {
        String key = trainerUsername + "_" + year;
        TrainerWorkloadSummary summary = workloadDB.get(key);
        if (summary == null) {
            summary = new TrainerWorkloadSummary(trainerUsername, "", "", false);
        }
        return summary;
    }

    public TrainerWorkloadAggregateResponse getAggregateSummary(String trainerUsername) {
        String prefix = trainerUsername + "_";
        Map<Integer, Map<Integer, Integer>> aggregateMap = new HashMap<>();
        String trainerFirstName = "";
        String trainerLastName = "";
        boolean isActive = true;
        for (Map.Entry<String, TrainerWorkloadSummary> entry : workloadDB.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String[] parts = key.split("_");
                int year = Integer.parseInt(parts[1]);
                TrainerWorkloadSummary summary = entry.getValue();
                trainerFirstName = summary.getTrainerFirstName();
                trainerLastName = summary.getTrainerLastName();
                isActive = summary.isActive();
                Map<Integer, Integer> yearData = aggregateMap.getOrDefault(year, new HashMap<>());
                for (Map.Entry<Integer, Integer> monthEntry : summary.getMonthlyWorkload().entrySet()) {
                    int month = monthEntry.getKey();
                    int duration = monthEntry.getValue();
                    yearData.put(month, yearData.getOrDefault(month, 0) + duration);
                }
                aggregateMap.put(year, yearData);
            }
        }
        List<YearSummary> yearSummaries = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : aggregateMap.entrySet()) {
            int year = entry.getKey();
            Map<Integer, Integer> monthMap = entry.getValue();
            List<MonthSummary> monthSummaries = monthMap.entrySet().stream().map(e -> new MonthSummary(e.getKey(), e.getValue())).sorted(Comparator.comparingInt(MonthSummary::getMonth)).toList();
            yearSummaries.add(new YearSummary(year, monthSummaries));
        }
        yearSummaries.sort(Comparator.comparingInt(YearSummary::getYear));
        return new TrainerWorkloadAggregateResponse(trainerUsername, trainerFirstName, trainerLastName, isActive, yearSummaries);
    }
}
