package com.imperva.stepping;

public class StatStepConfig {

    private  Boolean initCollector;
    private  Boolean enableStatForStep;
    private int reportReleaseTimeout;//* Seconds

    public StatStepConfig(){
        SteppingProperties stepProp = SteppingProperties.getInstance();
        initCollector = new Boolean(stepProp.getProperty("stepping.default.algo.statstep.enable"));
        reportReleaseTimeout = new Integer(stepProp.getProperty("stepping.default.algo.statstep.reportrelease.timeout"));
        enableStatForStep = new Boolean(stepProp.getProperty("stepping.default.step.statstep.enable"));
    }

    public Boolean isInitCollector() {
        return initCollector;
    }

    public void setIsInitCollector(Boolean isStatEnabledForStep) {
        initCollector = isStatEnabledForStep;
    }

    public Boolean isStatEnabledForStep() {
        return enableStatForStep;
    }

    public void setIsStatEnabledForStep(Boolean isStatEnabledForStep) {
        enableStatForStep = isStatEnabledForStep;
    }

    public int getReportReleaseTimeout() {
        return reportReleaseTimeout;
    }

    public void setReportReleaseTimeout(int reportReleaseTimeout) {
        this.reportReleaseTimeout = reportReleaseTimeout;
    }
}
