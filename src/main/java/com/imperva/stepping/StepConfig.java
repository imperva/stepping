package com.imperva.stepping;

import java.util.concurrent.TimeUnit;

public class StepConfig {
    private  String runningPeriodicCronDelay;
    private long runningInitialDelay;
    private long runningPeriodicDelay;
    private TimeUnit runningPeriodicDelayUnit;
    private int numOfNodes = 0;
    private boolean enableTickCallback;
    private IDistributionStrategy distributionStrategy = new All2AllDistributionStrategy();
    private int boundQueueCapacity;
    private StatStepConfig statStepConfig;//TODO stas move to algo, by default false, + in all steps add ability to set specifically and by def false + add ability to set the report emition timeout

    public StepConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Long(stepProp.getProperty("stepping.default.step.initialdelay"));
        runningPeriodicDelay = new Long(stepProp.getProperty("stepping.default.step.delay"));
        runningPeriodicDelayUnit = TimeUnit.valueOf(stepProp.getProperty("stepping.default.step.delay.unit").toUpperCase());
        enableTickCallback = new Boolean(stepProp.getProperty("stepping.default.step.enable.tickcallback"));
        boundQueueCapacity = new Integer(stepProp.getProperty("stepping.default.step.bound.queue"));
        statStepConfig = new StatStepConfig();
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

    public int getBoundQueueCapacity() {
        return boundQueueCapacity;
    }

    public void setBoundQueueCapacity(int boundQueueCapacity) {
        this.boundQueueCapacity = boundQueueCapacity;
    }

    public String getRunningPeriodicCronDelay() {
        return runningPeriodicCronDelay;
    }

    public void setRunningPeriodicCronDelay(String runningPeriodicCronDelay) {
        this.runningPeriodicCronDelay = runningPeriodicCronDelay;
    }

    public Boolean getIsStatEnabledForStep() {
        return statStepConfig.isStatEnabledForStep();
    }


    public void setStatEnabledForStep(Boolean isStatEnabledForStep) {
        this.statStepConfig.setIsStatEnabledForStep(isStatEnabledForStep);
    }
}