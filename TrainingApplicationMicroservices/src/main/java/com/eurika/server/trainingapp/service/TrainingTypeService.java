package com.eurika.server.trainingapp.service;

import com.eurika.server.trainingapp.dao.TrainingTypeDao;
import com.eurika.server.trainingapp.dto.TrainingTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeService {
    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainingTypeService(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    public List<TrainingTypeResponse> getAllTrainingTypes() {

        return trainingTypeDao.findAll().stream().map(type -> new TrainingTypeResponse(type.getId(), type.getTrainingTypeName())).toList();
    }
}