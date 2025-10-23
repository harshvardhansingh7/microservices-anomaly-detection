package com.watchtower.api.controller;

import com.watchtower.api.model.AnomalyResult;
import com.watchtower.api.service.AnomalyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@CrossOrigin(origins = "*")
public class AnomalyController {
    
    @Autowired
    private AnomalyService anomalyService;
    
    @GetMapping("/latest")
    public ResponseEntity<List<AnomalyResult>> getLatestAnomalies(
            @RequestParam(defaultValue = "10") int limit) {
        List<AnomalyResult> anomalies = anomalyService.getLatestAnomalies(limit);
        return ResponseEntity.ok(anomalies);
    }
    
    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<AnomalyResult>> getAnomaliesByService(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "50") int limit) {
        List<AnomalyResult> anomalies = anomalyService.getAnomaliesByService(serviceName, limit);
        return ResponseEntity.ok(anomalies);
    }
    
    @GetMapping("/timestamp")
    public ResponseEntity<List<AnomalyResult>> getAnomaliesByTimestamp(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<AnomalyResult> anomalies = anomalyService.getAnomaliesByTimeRange(from, to);
        return ResponseEntity.ok(anomalies);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Anomaly API is running");
    }
}