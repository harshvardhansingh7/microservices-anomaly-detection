package com.watchtower.producer.service;

import com.watchtower.producer.model.ServiceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class LogGeneratorService {
    
    private static final List<String> SERVICES = Arrays.asList(
        "OrderService", "PaymentService", "UserService", 
        "InventoryService", "ShippingService", "AuthService"
    );
    
    private final Random random = new Random();
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void generateLogs() {
        for (String service : SERVICES) {
            ServiceLog log = generateServiceLog(service);
            kafkaTemplate.send("microservice.logs", log);
            System.out.println("Generated log: " + log.getServiceName() + " - Latency: " + log.getLatency());
        }
    }
    
    private ServiceLog generateServiceLog(String serviceName) {
        // Simulate normal behavior with occasional anomalies
        double baseLatency = 50 + random.nextDouble() * 100;
        double latency = baseLatency;
        
        // 5% chance of high latency anomaly
        if (random.nextDouble() < 0.05) {
            latency = 500 + random.nextDouble() * 1000;
        }
        
        double errorRate = random.nextDouble() * 0.1; // 0-10% error rate
        // 3% chance of high error rate
        if (random.nextDouble() < 0.03) {
            errorRate = 0.3 + random.nextDouble() * 0.7;
        }
        
        int userCount = 100 + random.nextInt(900);
        int memoryUsage = 30 + random.nextInt(70);
        int cpuUsage = 20 + random.nextInt(60);
        
        return new ServiceLog(serviceName, latency, errorRate, userCount, memoryUsage, cpuUsage);
    }
}