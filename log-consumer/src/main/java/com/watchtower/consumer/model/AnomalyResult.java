package com.watchtower.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnomalyResult {
    private String anomalyId;
    private String serviceName;
    private double anomalyScore;

    @JsonProperty("anomaly")
    private boolean isAnomaly;

    private String timestamp;
    private ServiceLog originalLog;

    public AnomalyResult() {}

    public AnomalyResult(String serviceName, double anomalyScore, boolean isAnomaly, 
                        String timestamp, ServiceLog originalLog) {
        this.serviceName = serviceName;
        this.anomalyScore = anomalyScore;
        this.isAnomaly = isAnomaly;
        this.timestamp = timestamp;
        this.originalLog = originalLog;
        this.anomalyId = java.util.UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getAnomalyId() { return anomalyId; }
    public void setAnomalyId(String anomalyId) { this.anomalyId = anomalyId; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(double anomalyScore) { this.anomalyScore = anomalyScore; }
    
    public boolean isAnomaly() { return isAnomaly; }
    public void setAnomaly(boolean anomaly) { isAnomaly = anomaly; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public ServiceLog getOriginalLog() { return originalLog; }
    public void setOriginalLog(ServiceLog originalLog) { this.originalLog = originalLog; }
}