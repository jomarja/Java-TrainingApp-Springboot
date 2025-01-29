package com.trainignapp.trainingapp.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class CustomDatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    public CustomDatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return Health.up().withDetail("database", "Reachable").build();
            } else {
                return Health.down().withDetail("database", "Query Failed").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("db", "Not Reachable").build();
        }
    }
}
