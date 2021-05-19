package com.imperva.stepping;

public class AlgoConfig {
    private long runningInitialDelay;
    private long runningPeriodicDelay;
    private boolean enableTickCallback;
    private PerfSamplerStepConfig perfSamplerStepConfig;
    private IExceptionHandler customExceptionHandler;
    private String externalPropertiesPath;
    private StatStepConfig statStepConfig;

    public AlgoConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Long(stepProp.getProperty("stepping.default.algo.initialdelay"));
        runningPeriodicDelay = new Long(stepProp.getProperty("stepping.default.algo.delay"));
        enableTickCallback = new Boolean(stepProp.getProperty("stepping.default.algo.enable.tickcallback"));
        perfSamplerStepConfig = new PerfSamplerStepConfig();
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

    public boolean isEnableTickCallback() {
        return enableTickCallback;
    }

    public void setEnableTickCallback(boolean enableTickCallback) {
        this.enableTickCallback = enableTickCallback;
    }

    public PerfSamplerStepConfig getPerfSamplerStepConfig() {
        return perfSamplerStepConfig;
    }

    public IExceptionHandler getCustomExceptionHandler() {
        return customExceptionHandler;
    }

    public void setCustomExceptionHandler(IExceptionHandler customExceptionHandler) {
        this.customExceptionHandler = customExceptionHandler;
    }

    public String getExternalPropertiesPath() {
        return externalPropertiesPath;
    }

    public void setExternalPropertiesPath(String externalPropertiesPath) {
        this.externalPropertiesPath = externalPropertiesPath;
    }

    public Boolean getIsInitStatCollector() {
        return statStepConfig.isInitCollector();
    }

    public void setInitStatCollector(Boolean initStatCollector) {
        this.statStepConfig.setIsInitCollector(initStatCollector);
    }


    public int getReportReleaseTimeout() {
        return statStepConfig.getReportReleaseTimeout();
    }

    public void setReportReleaseTimeout(int timeout) {
        statStepConfig.setReportReleaseTimeout(timeout);
    }
}
