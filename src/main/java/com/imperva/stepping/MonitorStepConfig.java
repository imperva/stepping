package com.imperva.stepping;

public class MonitorStepConfig {

    private  Boolean initCollector;
    private  Boolean enableMonitoringForStep;
    private int reportReleaseTimeout;//* Seconds

    public MonitorStepConfig(){
        SteppingProperties stepProp = SteppingProperties.getInstance();
        initCollector = new Boolean(stepProp.getProperty("stepping.default.algo.monitorstep.enable"));
        reportReleaseTimeout = new Integer(stepProp.getProperty("stepping.default.algo.monitorstep.reportrelease.timeout"));
        enableMonitoringForStep = new Boolean(stepProp.getProperty("stepping.default.step.monitorstep.enable"));
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
}
