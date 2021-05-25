package com.imperva.stepping;

public class StatisticsReport {
    private String stepSenderId;
    private long avgProcessingTime;
    private int avgChunkSize;
    private long latestQSize;

    public long getAvgProcessingTime() {
        return avgProcessingTime;
    }

    public void setAvgProcessingTime(long avgProcessingTime) {
        this.avgProcessingTime = avgProcessingTime;
    }

    public int getAvgChunkSize() {
        return avgChunkSize;
    }

    public void setAvgChunkSize(int avgChunkSize) {
        this.avgChunkSize = avgChunkSize;
    }

    public long getLatestQSize() {
        return latestQSize;
    }

    public void setLatestQSize(long latestQSize) {
        this.latestQSize = latestQSize;
    }

    public String getStepSenderId() {
        return stepSenderId;
    }

    public void setStepSenderId(String stepSenderId) {
        this.stepSenderId = stepSenderId;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("**** Step " + getStepSenderId() + ":\n");
        stringBuilder.append("AvgChunkSize " + getAvgChunkSize() + "\n");
        stringBuilder.append("AvgProcessTimeSize " + getAvgProcessingTime()+ "\n");
        stringBuilder.append("QueueSize " + getLatestQSize() + "\n");
        stringBuilder.append("****************");

        return stringBuilder.toString();

    }
}
