package com.watchtower.producer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ServiceLog {
    private String serviceName;
    private double latency;
    private double errorRate;
    private int userCount;
    private int memoryUsage;
    private int cpuUsage;

    @JsonProperty("timestamp")
    private String timestamp; // Changed from LocalDateTime to String

    // Default constructor
    public ServiceLog() {}

    // Constructor with parameters
    public ServiceLog(String serviceName, double latency, double errorRate,
                      int userCount, int memoryUsage, int cpuUsage) {
        this.serviceName = serviceName;
        this.latency = latency;
        this.errorRate = errorRate;
        this.userCount = userCount;
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
        this.timestamp = java.time.LocalDateTime.now().toString(); // ISO string
    }

    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getLatency() { return latency; }
    public void setLatency(double latency) { this.latency = latency; }

    public double getErrorRate() { return errorRate; }
    public void setErrorRate(double errorRate) { this.errorRate = errorRate; }

    public int getUserCount() { return userCount; }
    public void setUserCount(int userCount) { this.userCount = userCount; }

    public int getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(int memoryUsage) { this.memoryUsage = memoryUsage; }

    public int getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(int cpuUsage) { this.cpuUsage = cpuUsage; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}