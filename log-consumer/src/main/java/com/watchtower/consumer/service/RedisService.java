
package com.watchtower.consumer.service;

import com.watchtower.consumer.model.AnomalyResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    private static final String ANOMALY_KEY_PREFIX = "anomaly:";
    private static final String SERVICE_ANOMALIES_KEY = "anomalies:service:";
    private static final long TTL_HOURS = 24;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void storeAnomaly(AnomalyResult anomaly) {
        log.info("🧩 Trying to store anomaly in Redis: {}", anomaly);

        try {
            String anomalyKey = ANOMALY_KEY_PREFIX + anomaly.getAnomalyId();
            String serviceKey = SERVICE_ANOMALIES_KEY + anomaly.getServiceName();

            redisTemplate.opsForValue().set(anomalyKey, anomaly, TTL_HOURS, TimeUnit.HOURS);
            redisTemplate.opsForZSet().add(serviceKey, anomaly.getAnomalyId(), System.currentTimeMillis());
            redisTemplate.opsForList().leftPush("anomalies:recent", anomalyKey);
            redisTemplate.opsForList().trim("anomalies:recent", 0, 99);

            log.info("✅ Saved anomaly [{}] for service [{}]", anomaly.getAnomalyId(), anomaly.getServiceName());
        } catch (Exception e) {
            log.error("❌ Error saving anomaly to Redis: {}", e.getMessage(), e);
        }
    }
}
