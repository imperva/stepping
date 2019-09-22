package com.imperva.stepping;

import java.util.concurrent.TimeUnit;

public class StepConfig {
    private long runningInitialDelay;
    private long runningPeriodicDelay;
    private TimeUnit runningPeriodicDelayUnit;
    private int numOfNodes = 0;
    private String reducerID;
    private boolean enableTickCallback;
    private IDistributionStrategy distributionStrategy = new All2AllDistributionStrategy();
    private Step reducerStep;

    public StepConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Long(stepProp.getProperty("stepping.default.step.initialdelay"));
        runningPeriodicDelay = new Long(stepProp.getProperty("stepping.default.step.delay"));
        runningPeriodicDelayUnit = TimeUnit.valueOf(stepProp.getProperty("stepping.default.step.delay.unit").toUpperCase());
        enableTickCallback = new Boolean(stepProp.getProperty("stepping.default.step.enable.tickcallback"));
    }

    public long getRunningInitialDelay() {
        return runningInitialDelay;
    }

    public void setRunningInitialDelay(int runningInitialDelay) {
        this.runningInitialDelay = runningInitialDelay;
    }

    public long getRunningPeriodicDelay() {
        return runningPeriodicDelay;
    }

    public void setRunningPeriodicDelay(int runningPeriodicDelay) {
        this.runningPeriodicDelay = runningPeriodicDelay;
    }

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }

    public boolean isEnableTickCallback() {
        return enableTickCallback;
    }

    public void setEnableTickCallback(boolean enableTickCallback) {
        this.enableTickCallback = enableTickCallback;
    }

    public IDistributionStrategy getDistributionStrategy() {
        return distributionStrategy;
    }

    public void setDistributionStrategy(IDistributionStrategy distributionStrategy) {
        this.distributionStrategy = distributionStrategy;
    }

    public TimeUnit getRunningPeriodicDelayUnit() {
        return runningPeriodicDelayUnit;
    }

    public void setRunningPeriodicDelayUnit(TimeUnit runningPeriodicDelayUnit) {
        this.runningPeriodicDelayUnit = runningPeriodicDelayUnit;
    }

    public String getReducerID() {
        return reducerID;
    }

    public void setReducerID(String reducerID) {
        this.reducerID = reducerID;
    }



     Step getReducerStep() {
        return reducerStep;
    }


    void setReducerStep(Step reducerStep) {
        this.reducerStep = reducerStep;
    }
}