package com.watchtower.consumer.service;

//import com.watchtower.consumer.model.ServiceLog;
//import com.watchtower.consumer.model.AnomalyResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class AnomalyDetectionService {
//
//    @Value("${ai.service.url:http://127.0.0.1:5000}")
//    private String aiServiceUrl;
//
//    @Autowired
//    private RedisService redisService;
//
//    @Autowired
//    private RestTemplate restTemplate; // use autowired RestTemplate
//
//    public void detectAnomalies(List<ServiceLog> logs) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<List<ServiceLog>> request = new HttpEntity<>(logs, headers);
//
//            AnomalyResult[] results = restTemplate.postForObject(
//                    aiServiceUrl + "/detect-anomalies",
//                    request,
//                    AnomalyResult[].class
//            );
//
//            if (results != null) {
//                for (AnomalyResult result : results) {
//                    if (result.isAnomaly()) {
//                        redisService.storeAnomaly(result);
//                        System.out.println("Detected anomaly: " + result.getServiceName() +
//                                " Score: " + result.getAnomalyScore());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error calling AI service: " + e.getMessage());
//        }
//    }
//}

import com.watchtower.consumer.model.AnomalyResult;
import com.watchtower.consumer.model.ServiceLog;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AnomalyDetectionService {

    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String aiServiceUrl = "http://127.0.0.1:5000";

    public AnomalyDetectionService(RestTemplate restTemplate, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.redisService = redisService;
    }

    public void detectAnomalies(List<ServiceLog> logs) {
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
            if (results != null) {
                for (AnomalyResult result : results) {
                    if (result.isAnomaly()) {
                        redisService.storeAnomaly(result);
                        System.out.println("Detected anomaly: " + result.getServiceName() +
                                " Score: " + result.getAnomalyScore());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
        }
    }
}
