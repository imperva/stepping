package com.imperva.stepping;

public class AlgoConfig {
    private long runningInitialDelay;
    private long runningPeriodicDelay;
    private boolean enableTickCallback;

    public AlgoConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Long(stepProp.getProperty("stepping.default.algo.initialdelay"));
        runningPeriodicDelay = new Long(stepProp.getProperty("stepping.default.algo.delay"));
        enableTickCallback = new Boolean(stepProp.getProperty("stepping.default.algo.enable.tickcallback"));
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

    public boolean isEnableTickCallback() {
        return enableTickCallback;
    }

    public void setEnableTickCallback(boolean enableTickCallback) {
        this.enableTickCallback = enableTickCallback;
    }
}