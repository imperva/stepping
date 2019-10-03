package com.imperva.stepping;

public class PerfSamplerStepConfig {
    private boolean enable;
    private String packages;
    private int reportInterval;

    public PerfSamplerStepConfig(){
        SteppingProperties stepProp = SteppingProperties.getInstance();
        enable = new Boolean(stepProp.getProperty("stepping.default.algo.perfsampler.enable"));
        packages = stepProp.getProperty("stepping.default.algo.perfsampler.packages");
        reportInterval = new Integer(stepProp.getProperty("stepping.default.algo.perfsampler.reportinterval"));
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public int getReportInterval() {
        return reportInterval;
    }

    public void setReportInterval(int reportInterval) {
        this.reportInterval = reportInterval;
    }
}
