package com.imperva.stepping;

public class StatisticsReport {
    private long avgProcessingTime;
    private double avgChunkSize;
    private long latestQSize;

    public long getAvgProcessingTime() {
        return avgProcessingTime;
    }

    public void setAvgProcessingTime(long avgProcessingTime) {
        this.avgProcessingTime = avgProcessingTime;
    }

    public double getAvgChunkSize() {
        return avgChunkSize;
    }

    public void setAvgChunkSize(double avgChunkSize) {
        this.avgChunkSize = avgChunkSize;
    }

    public long getLatestQSize() {
        return latestQSize;
    }

    public void setLatestQSize(long latestQSize) {
        this.latestQSize = latestQSize;
    }
}