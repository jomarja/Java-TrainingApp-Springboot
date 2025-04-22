Feature: Trainer Workload Service

  Background:
    Given the MongoDB is clean

  Scenario: Creating a new summary for a brand‑new trainer
    Given the MongoDB is clean
    When I send a workload request with
      | trainerUsername | firstName | lastName | active | trainingDate | duration | action |
      | john_doe        | John      | Doe      | true   | 2025-04-10   | 3        | ADD    |
    When I request monthly summary for trainer "john_doe" year 2025
    Then the summary for "john_doe" in year 2025 month 4 should have total duration 3

  Scenario: Adding on top of existing summary
    Given an existing summary for "john_doe" in "2025-04" with duration 3
    When I send a workload request with
      | trainerUsername | firstName | lastName | active | trainingDate | duration | action |
      | john_doe        | John      | Doe      | true   | 2025-04-15   | 2        | ADD    |
    When I request monthly summary for trainer "john_doe" year 2025
    Then the summary for "john_doe" in year 2025 month 4 should have total duration 5

  Scenario: Deleting more than exists does not go below zero
    Given an existing summary for "john_doe" in "2025-04" with duration 2
    When I send a workload request with
      | trainerUsername | firstName | lastName | active | trainingDate | duration | action |
      | john_doe        | John      | Doe      | true   | 2025-04-20   | 5        | DELETE |
    When I request monthly summary for trainer "john_doe" year 2025
    Then the summary for "john_doe" in year 2025 month 4 should have total duration 0

  Scenario: Querying a non‑existent trainer returns empty
    When I request monthly summary for trainer "unknown" year 2025
    Then I get an empty workload map
