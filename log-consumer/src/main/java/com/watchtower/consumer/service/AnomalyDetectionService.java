package com.watchtower.consumer.service;


import com.watchtower.consumer.model.AnomalyResult;
import com.watchtower.consumer.model.ServiceLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AnomalyDetectionService {

    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String aiServiceUrl;

    public AnomalyDetectionService(
            RestTemplate restTemplate,
            RedisService redisService,
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.redisService = redisService;
        this.aiServiceUrl = aiServiceUrl;
    }

    public void detectAnomalies(List<ServiceLog> logs) {
        if (logs == null || logs.isEmpty()) {
            System.out.println("No logs to analyze for anomalies.");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<ServiceLog>> request = new HttpEntity<>(logs, headers);

            ResponseEntity<List<AnomalyResult>> response = restTemplate.exchange(
                    aiServiceUrl + "/detect-anomalies",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<List<AnomalyResult>>() {}
            );

            List<AnomalyResult> results = response.getBody();

            if (results != null && !results.isEmpty()) {
                for (AnomalyResult result : results) {
                    if (result.isAnomaly()) {
                        redisService.storeAnomaly(result);
                        System.out.printf(
                                "🚨 Detected anomaly in service '%s' (Score: %.2f)%n",
                                result.getServiceName(), result.getAnomalyScore()
                        );
                    }
                }
            } else {
                System.out.println("✅ No anomalies detected in current batch.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error calling AI service (" + aiServiceUrl + "): " + e.getMessage());
        }
    }
}
