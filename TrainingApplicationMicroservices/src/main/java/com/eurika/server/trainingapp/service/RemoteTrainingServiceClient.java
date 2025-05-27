package com.eurika.server.trainingapp.service;

import com.eurika.server.trainingapp.dto.TrainerWorkloadAggregateResponse;
import com.eurika.server.trainingapp.dto.TrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoteTrainingServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(RemoteTrainingServiceClient.class);
    @Value("${workload.service.url:http://trainer-workload-microservice}")
    private String workloadServiceUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public RemoteTrainingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "workloadServiceCB", fallbackMethod = "fallbackUpdateWorkloadWithTransaction")
    public void callUpdateWorkloadWithTransaction(String txnId, TrainerWorkloadRequest request) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = null;
        if (attributes != null) {
            token = attributes.getRequest().getHeader("Authorization");
            logger.info("[Transaction ID: {}] Forwarding token: {}", txnId, token);
        } else {
            logger.warn("[Transaction ID: {}] No request attributes found; token not propagated.", txnId);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", token);
        }
        headers.set("Transaction-Id", txnId);
        HttpEntity<TrainerWorkloadRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForEntity(workloadServiceUrl + "/api/workload/update", entity, Void.class);
    }

    public void fallbackUpdateWorkloadWithTransaction(String txnId, TrainerWorkloadRequest request, Throwable t) {
        logger.error("[Transaction ID: {}] Fallback for update workload for trainer {}: {}", txnId, request.getTrainerUsername(), t.getMessage());
    }

    @CircuitBreaker(name = "workloadServiceCB", fallbackMethod = "fallbackGetAggregateSummary")
    public TrainerWorkloadAggregateResponse getAggregateSummary(String username) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = null;
        if (attributes != null) {
            token = attributes.getRequest().getHeader("Authorization");
            logger.info("Forwarding token for aggregate summary: {}", token);
        } else {
            logger.warn("No request attributes found for aggregate summary; token not propagated.");
        }
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", token);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("username", username);
        ResponseEntity<TrainerWorkloadAggregateResponse> responseEntity = restTemplate.exchange(workloadServiceUrl + "/api/workload/aggregate/{username}", HttpMethod.GET, entity, TrainerWorkloadAggregateResponse.class, uriVariables);
        return responseEntity.getBody();
    }

    public TrainerWorkloadAggregateResponse fallbackGetAggregateSummary(String username, Throwable t) {
        logger.error("Fallback for getAggregateSummary for trainer {}: {}", username, t.getMessage());
        TrainerWorkloadAggregateResponse defaultResponse = new TrainerWorkloadAggregateResponse();
        defaultResponse.setTrainerUsername(username);
        defaultResponse.setTrainerFirstName("");
        defaultResponse.setTrainerLastName("");
        defaultResponse.setActive(false);
        defaultResponse.setYears(java.util.Collections.emptyList());
        return defaultResponse;
    }
}
