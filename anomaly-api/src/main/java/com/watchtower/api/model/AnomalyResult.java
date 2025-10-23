package com.watchtower.api.model;

public class AnomalyResult {
    private String anomalyId;
    private String serviceName;
    private double anomalyScore;
    private boolean anomaly;
    private String timestamp;
    private Object originalLog;  // Can be LinkedHashMap if stored from consumer

    public AnomalyResult() {}

    // Getters and Setters
    public String getAnomalyId() { return anomalyId; }
    public void setAnomalyId(String anomalyId) { this.anomalyId = anomalyId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(double anomalyScore) { this.anomalyScore = anomalyScore; }

    public boolean isAnomaly() { return anomaly; }
    public void setAnomaly(boolean anomaly) { this.anomaly = anomaly; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Object getOriginalLog() { return originalLog; }
    public void setOriginalLog(Object originalLog) { this.originalLog = originalLog; }
}