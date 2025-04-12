package com.trainingapp.microservice.service;

import com.trainingapp.microservice.dto.TrainerWorkloadRequest;
import com.trainingapp.microservice.dto.TrainerWorkloadSummary;
import com.trainingapp.microservice.mongo.MonthRecord;
import com.trainingapp.microservice.mongo.TrainerTrainingSummary;
import com.trainingapp.microservice.mongo.YearRecord;
import com.trainingapp.microservice.repository.TrainerTrainingSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainerMongoService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerMongoService.class);
    private final TrainerTrainingSummaryRepository repository;

    public TrainerMongoService(TrainerTrainingSummaryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void updateTrainerSummary(TrainerWorkloadRequest request, String transactionId) {
        logger.info("Transaction {}: Received update event for trainer: {}", transactionId, request.getTrainerUsername());
        int year = extractYear(request.getTrainingDate());
        int month = extractMonth(request.getTrainingDate());

        TrainerTrainingSummary summary = repository.findByTrainerUsername(request.getTrainerUsername());
        if (summary == null) {
            summary = createNewSummary(request, year, month, transactionId);
        } else {
            updateExistingSummary(summary, request, year, month, transactionId);
        }
        repository.save(summary);
        logger.info("Transaction {}: Successfully updated trainer summary for {}", transactionId, request.getTrainerUsername());
    }

    private TrainerTrainingSummary createNewSummary(TrainerWorkloadRequest request, int year, int month, String transactionId) {
        TrainerTrainingSummary summary = new TrainerTrainingSummary();
        summary.setTrainerUsername(request.getTrainerUsername());
        summary.setTrainerFirstName(request.getTrainerFirstName());
        summary.setTrainerLastName(request.getTrainerLastName());
        summary.setTrainerStatus(request.isActive());

        MonthRecord newMonth = createNewMonthRecord(month, request.getTrainingDuration());
        YearRecord newYear = createNewYearRecord(year, newMonth);
        List<YearRecord> yearRecords = new ArrayList<>();
        yearRecords.add(newYear);
        summary.setYears(yearRecords);

        logger.info("Transaction {}: Creating new record for trainer {} with year {} and month {} with duration {}", transactionId, request.getTrainerUsername(), year, month, request.getTrainingDuration());
        return summary;
    }

    private void updateExistingSummary(TrainerTrainingSummary summary, TrainerWorkloadRequest request, int year, int month, String transactionId) {
        summary.setTrainerFirstName(request.getTrainerFirstName());
        summary.setTrainerLastName(request.getTrainerLastName());
        summary.setTrainerStatus(request.isActive());

        List<YearRecord> years = summary.getYears();
        if (years == null) {
            years = new ArrayList<>();
        }

        YearRecord yearRecord = years.stream().filter(y -> y.getYear() == year).findFirst().orElse(null);
        if (yearRecord == null) {
            MonthRecord newMonth = createNewMonthRecord(month, request.getTrainingDuration());
            YearRecord newYear = createNewYearRecord(year, newMonth);
            years.add(newYear);
            logger.info("Transaction {}: Adding new year record {} for trainer {}", transactionId, year, request.getTrainerUsername());
        } else {
            List<MonthRecord> months = yearRecord.getMonths();
            if (months == null) {
                months = new ArrayList<>();
                yearRecord.setMonths(months);
            }
            MonthRecord monthRecord = months.stream().filter(m -> m.getMonth() == month).findFirst().orElse(null);
            if (monthRecord == null) {
                monthRecord = createNewMonthRecord(month, request.getTrainingDuration());
                months.add(monthRecord);
                logger.info("Transaction {}: Adding new month record {} in year {} for trainer {}", transactionId, month, year, request.getTrainerUsername());
            } else {
                updateMonthRecord(monthRecord, request, transactionId, month, year, request.getTrainerUsername());
            }
        }
        summary.setYears(years);
    }

    private MonthRecord createNewMonthRecord(int month, int duration) {
        MonthRecord monthRecord = new MonthRecord();
        monthRecord.setMonth(month);
        monthRecord.setTrainingSummaryDuration(duration);
        return monthRecord;
    }

    private YearRecord createNewYearRecord(int year, MonthRecord monthRecord) {
        YearRecord yearRecord = new YearRecord();
        yearRecord.setYear(year);
        List<MonthRecord> monthRecords = new ArrayList<>();
        monthRecords.add(monthRecord);
        yearRecord.setMonths(monthRecords);
        return yearRecord;
    }

    private void updateMonthRecord(MonthRecord monthRecord, TrainerWorkloadRequest request, String transactionId, int month, int year, String trainerUsername) {
        if ("ADD".equalsIgnoreCase(request.getActionType())) {
            int updatedDuration = monthRecord.getTrainingSummaryDuration() + request.getTrainingDuration();
            monthRecord.setTrainingSummaryDuration(updatedDuration);
            logger.info("Transaction {}: Updated month {} in year {} for trainer {}. Added {}. New duration: {}", transactionId, month, year, trainerUsername, request.getTrainingDuration(), updatedDuration);
        } else if ("DELETE".equalsIgnoreCase(request.getActionType())) {
            int updatedDuration = Math.max(0, monthRecord.getTrainingSummaryDuration() - request.getTrainingDuration());
            monthRecord.setTrainingSummaryDuration(updatedDuration);
            logger.info("Transaction {}: Updated month {} in year {} for trainer {}. Deleted {}. New duration: {}", transactionId, month, year, trainerUsername, request.getTrainingDuration(), updatedDuration);
        }
    }

    public TrainerWorkloadSummary getMonthlySummary(String trainerUsername, int year) {
        TrainerTrainingSummary trainingSummary = repository.findByTrainerUsername(trainerUsername);
        if (trainingSummary == null) {
            return new TrainerWorkloadSummary(trainerUsername, "", "", false);
        }
        Map<Integer, Integer> monthlyWorkload = extractMonthlyWorkload(trainingSummary, year);
        return new TrainerWorkloadSummary(trainingSummary.getTrainerUsername(), trainingSummary.getTrainerFirstName(), trainingSummary.getTrainerLastName(), trainingSummary.isTrainerStatus(), monthlyWorkload);
    }

    private Map<Integer, Integer> extractMonthlyWorkload(TrainerTrainingSummary trainingSummary, int targetYear) {
        if (trainingSummary.getYears() == null) {
            return Collections.emptyMap();
        }

        for (YearRecord yr : trainingSummary.getYears()) {
            if (yr.getYear() == targetYear) {
                if (yr.getMonths() == null) {
                    return Collections.emptyMap();
                }
                return yr.getMonths().stream().collect(Collectors.toMap(MonthRecord::getMonth, MonthRecord::getTrainingSummaryDuration));
            }
        }
        return Collections.emptyMap();
    }

    private int extractYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private int extractMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }
}