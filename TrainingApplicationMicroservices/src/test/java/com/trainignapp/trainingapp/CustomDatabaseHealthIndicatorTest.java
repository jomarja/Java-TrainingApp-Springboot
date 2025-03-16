package com.trainignapp.trainingapp;

import com.trainignapp.trainingapp.actuator.CustomDatabaseHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomDatabaseHealthIndicatorTest {
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private ResultSet resultSet;
    private CustomDatabaseHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        healthIndicator = new CustomDatabaseHealthIndicator(dataSource);

        // Mocking DataSource behavior
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT 1")).thenReturn(resultSet);
    }

    @Test
    void shouldReturnHealthUpWhenDatabaseIsReachable() throws Exception {
        // Mocking a successful query result
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // Execute the health check
        Health health = healthIndicator.health();

        // Assertions
        assertEquals(Health.up().withDetail("database", "Reachable").build(), health);

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT 1");
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getInt(1);
    }

    @Test
    void shouldReturnHealthDownWhenQueryFails() throws Exception {
        // Mocking a query failure
        when(resultSet.next()).thenReturn(false);

        // Execute the health check
        Health health = healthIndicator.health();

        // Assertions
        assertEquals(Health.down().withDetail("database", "Query Failed").build(), health);

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT 1");
        verify(resultSet, times(1)).next();
    }

    @Test
    void shouldReturnHealthDownWhenExceptionOccurs() throws Exception {
        // Mocking an exception during connection
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // Execute the health check
        Health health = healthIndicator.health();

        // Assertions
        assertEquals("Not Reachable", health.getDetails().get("db"));

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
    }
}
