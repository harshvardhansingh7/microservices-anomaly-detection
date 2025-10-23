package com.watchtower.consumer.service;

import com.watchtower.consumer.model.AnomalyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    
    private static final String ANOMALY_KEY_PREFIX = "anomaly:";
    private static final String SERVICE_ANOMALIES_KEY = "anomalies:service:";
    private static final long TTL_HOURS = 24;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void storeAnomaly(AnomalyResult anomaly) {
        String anomalyKey = ANOMALY_KEY_PREFIX + anomaly.getAnomalyId();
        String serviceKey = SERVICE_ANOMALIES_KEY + anomaly.getServiceName();
        
        // Store individual anomaly
        redisTemplate.opsForValue().set(anomalyKey, anomaly, TTL_HOURS, TimeUnit.HOURS);
        
        // Add to service-specific sorted set (by timestamp)
        redisTemplate.opsForZSet().add(
            serviceKey, 
            anomaly.getAnomalyId(), 
            System.currentTimeMillis()
        );
        
        // Add to global recent anomalies
        redisTemplate.opsForList().leftPush("anomalies:recent", anomalyKey);
        
        // Trim recent list to last 100
        redisTemplate.opsForList().trim("anomalies:recent", 0, 99);
    }
}