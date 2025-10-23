package com.watchtower.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchtower.api.model.AnomalyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnomalyService {

    private static final String ANOMALY_KEY_PREFIX = "anomaly:";
    private static final String SERVICE_ANOMALIES_KEY = "anomalies:service:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Get latest anomalies
    public List<AnomalyResult> getLatestAnomalies(int limit) {
        List<Object> anomalyKeys = redisTemplate.opsForList()
                .range("anomalies:recent", 0, limit - 1);
        return getAnomaliesByKeys(anomalyKeys);
    }

    // Get anomalies by service
    public List<AnomalyResult> getAnomaliesByService(String serviceName, int limit) {
        String serviceKey = SERVICE_ANOMALIES_KEY + serviceName;
        Set<Object> anomalyIds = redisTemplate.opsForZSet()
                .reverseRange(serviceKey, 0, limit - 1);

        List<Object> anomalyKeys = anomalyIds.stream()
                .map(id -> ANOMALY_KEY_PREFIX + id)
                .collect(Collectors.toList());

        return getAnomaliesByKeys(anomalyKeys);
    }

    // Get anomalies by time range
    public List<AnomalyResult> getAnomaliesByTimeRange(LocalDateTime from, LocalDateTime to) {
        List<AnomalyResult> allAnomalies = getLatestAnomalies(1000); // large set

        return allAnomalies.stream()
                .filter(anomaly -> {
                    LocalDateTime anomalyTime = LocalDateTime.parse(anomaly.getTimestamp());
                    return !anomalyTime.isBefore(from) && !anomalyTime.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    // Convert LinkedHashMap -> AnomalyResult
    private List<AnomalyResult> getAnomaliesByKeys(List<Object> keys) {
        List<AnomalyResult> anomalies = new ArrayList<>();

        for (Object key : keys) {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                // ✅ Convert LinkedHashMap to AnomalyResult
                AnomalyResult anomaly = objectMapper.convertValue(obj, AnomalyResult.class);
                anomalies.add(anomaly);
            }
        }
        return anomalies;
    }
}