package com.watchtower.consumer.service;

import com.watchtower.consumer.model.ServiceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final int BATCH_SIZE = 10;
    private final BlockingQueue<ServiceLog> logQueue = new LinkedBlockingQueue<>();

    @Autowired
    private AnomalyDetectionService anomalyDetectionService;

    @KafkaListener(topics = "microservice.logs", groupId = "log-consumer-group")
    public void consumeLog(@Payload ServiceLog log,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.OFFSET) long offset) {
        logger.info("📥 Received log from topic: {}, offset: {}", topic, offset);
        logger.info("📊 Log data: {} - Latency: {}, ErrorRate: {}",
                log.getServiceName(), log.getLatency(), log.getErrorRate());

        logQueue.offer(log);
        if (logQueue.size() >= BATCH_SIZE) processBatch();
    }

    private void processBatch() {
        try {
            List<ServiceLog> batch = new ArrayList<>();
            logQueue.drainTo(batch, BATCH_SIZE);

            if (!batch.isEmpty()) {
                logger.info("🔍 Processing batch of {} logs for anomaly detection", batch.size());
                anomalyDetectionService.detectAnomalies(batch);
            }
        } catch (Exception e) {
            logger.error("❌ Error processing batch", e);
        }
    }

    // Method to manually trigger batch processing (for testing)
    public void triggerBatchProcessing() {
        if (!logQueue.isEmpty()) {
            processBatch();
        }
    }
}