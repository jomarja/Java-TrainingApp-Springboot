package com.trainingapp.microservice.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainerTrainingSummaries")
public class TrainerTrainingSummary {
    @Id
    private String id;


    @Indexed(unique = true)
    private String trainerUsername;


    @Indexed
    private String trainerFirstName;

    @Indexed
    private String trainerLastName;

    private boolean trainerStatus;

    private List<YearRecord> years;
}
