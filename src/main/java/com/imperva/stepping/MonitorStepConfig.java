package com.imperva.stepping;

public class MonitorStepConfig {

    private  Boolean initCollector;
    private  Boolean enableMonitoringForStep;
    private int reportReleaseTimeout;//* Seconds
    private int metadataEmmitTimeout;//* Seconds

    public MonitorStepConfig(){
        SteppingProperties stepProp = SteppingProperties.getInstance();
        initCollector = new Boolean(stepProp.getProperty("stepping.default.algo.monitorstep.enable"));
        reportReleaseTimeout = new Integer(stepProp.getProperty("stepping.default.algo.monitorstep.reportrelease.timeout"));
        enableMonitoringForStep = new Boolean(stepProp.getProperty("stepping.default.step.monitorstep.enable"));
        metadataEmmitTimeout = new Integer(stepProp.getProperty("stepping.default.algo.monitorstep.metadataemmit.timeout"));
    }

    public Boolean isInitCollector() {
        return initCollector;
    }

    public void setIsInitCollector(Boolean isMonitorEnabledForStep) {
        initCollector = isMonitorEnabledForStep;
    }

    public Boolean isMonitorEnabledForStep() {
        return enableMonitoringForStep;
    }

    public void setIsMonitorEnabledForStep(Boolean isMonitoringEnabledForStep) {
        enableMonitoringForStep = isMonitoringEnabledForStep;
    }

    public int getReportReleaseTimeout() {
        return reportReleaseTimeout;
    }

    public void setReportReleaseTimeout(int reportReleaseTimeout) {
        this.reportReleaseTimeout = reportReleaseTimeout;
    }

    public int getMetadataEmmitTimeout() {
        return metadataEmmitTimeout;
    }

    public void setMetadataEmmitTimeout(int metadataEmmitTimeout) {
        this.metadataEmmitTimeout = metadataEmmitTimeout;
    }
}
