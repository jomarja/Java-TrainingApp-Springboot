//package com.trainignapp.trainingapp.Storage;
//
//import com.trainignapp.trainingapp.model.Trainee;
//import com.trainignapp.trainingapp.model.Trainer;
//import com.trainignapp.trainingapp.model.Training;
//
//
//import com.trainignapp.trainingapp.service.TraineeService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//public class MapsImplementation {
//
//    private Map<Integer, Trainee> trainees = new HashMap<>();
//    private Map<Integer, Trainer> trainers = new HashMap<>();
//    private Map<String, Training> trainings = new HashMap<>();
//
//
//    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
//
//    public void MapsImpl() throws IOException {
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/training.properties")) {
//            if (input == null) {
//                throw new RuntimeException("Properties file not found in resources folder");
//            }
//            logger.info("The file was found", input);
//
//            Properties prop = new Properties();
//            prop.load(input);
//
//            trainees.put(1, new Trainee(
//                    prop.getProperty("trainee1.firstName"),
//                    prop.getProperty("trainee1.lastName"),
//                    prop.getProperty("trainee1.username"),
//                    prop.getProperty("trainee1.password"),
//                    Boolean.parseBoolean(prop.getProperty("trainee1.isActive")),
//                    prop.getProperty("trainee1.address"),
//                    Integer.parseInt(prop.getProperty("trainee1.id"))
//            ));
//
//            trainers.put(1, new Trainer(
//                    Integer.parseInt(prop.getProperty("trainer1.id")),
//                    prop.getProperty("trainer1.firstName"),
//                    prop.getProperty("trainer1.lastName"),
//                    prop.getProperty("trainer1.username"),
//                    prop.getProperty("trainer1.password"),
//                    Boolean.parseBoolean(prop.getProperty("trainer1.isActive"))
//            ));
//
//            trainings.put("training1", new Training(
//                    Integer.parseInt(prop.getProperty("training_trainee1.id")),
//                    Integer.parseInt(prop.getProperty("training_trainer1.id")),
//                    prop.getProperty("training1.name"),
//                    Integer.parseInt(prop.getProperty("training.duration"))
//            ));
//        } catch (IOException ex) {
//            throw new RuntimeException("Error loading properties file", ex);
//        }
//    }
//
//    public Map<Integer, Trainee> getTrainees() {
//        return this.trainees;
//    }
//
//    public Map<Integer, Trainer> getTrainers() {
//        return trainers;
//    }
//
//    public Map<String, Training> getTrainings() {
//        return trainings;
//    }
//}
