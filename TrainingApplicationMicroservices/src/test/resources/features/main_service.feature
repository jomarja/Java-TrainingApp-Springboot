Feature: Training service component tests

  Scenario: Retrieve all trainings for a trainee without filters
    Given a trainee "alice" exists
    And the following trainings exist for trainee "alice":
      | trainingName | trainingDate | trainingDuration | trainerUsername | trainingTypeName |
      | Java 101     | 2025‑01‑01   | 2                | trainer1        | Java             |
      | Spring       | 2025‑02‑01   | 3                | trainer2        | Spring           |
    When I request trainee trainings for "alice" from "2025‑01‑01" to "2025‑12‑31"
    Then I should get 2 trainings

  Scenario: Filter trainee trainings by trainer
    Given a trainee "alice" exists
    And the following trainings exist for trainee "alice":
      | trainingName | trainingDate | trainingDuration | trainerUsername | trainingTypeName |
      | Java 101     | 2025‑01‑01   | 2                | trainer1        | Java             |
      | Spring       | 2025‑02‑01   | 3                | trainer2        | Spring           |
    When I request trainee trainings for "alice" from "2025‑01‑01" to "2025‑12‑31" filtered by trainer "trainer1"
    Then I should get 1 trainings

  Scenario: Retrieve all trainings for a trainer without filters
    Given a trainer "trainer1" exists
    And the following trainings exist for trainer "trainer1":
      | trainingName | trainingDate | trainingDuration | traineeUsername | trainingTypeName |
      | Java 101     | 2025‑01‑01   | 2                | alice           | Java             |
      | Spring       | 2025‑02‑01   | 3                | bob             | Spring           |
    When I request trainer trainings for "trainer1" from "2025‑01‑01" to "2025‑12‑31"
    Then I should get 2 trainer trainings
