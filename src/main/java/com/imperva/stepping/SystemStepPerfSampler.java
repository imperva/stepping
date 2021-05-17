package com.imperva.stepping;

import com.imperva.sampler.ThreadsSampler;
import com.imperva.sampler.outputer.LoggerSamplingOutputer;

import java.io.IOException;

@SystemStep
class SystemStepPerfSampler implements Step {
    private final int reportInterval;
    private final String packges;
    private ThreadsSampler ts;

    SystemStepPerfSampler(int reportInterval, String packges){
        this.reportInterval = reportInterval;
        this.packges = packges;
    }

    @Override
    public String getId(){
        return "SYSTEM_STEP_PERF_SAMPLER";
    }

    @Override
    public void init(Container cntr, Shouter shouter) {
        ts = new ThreadsSampler();
        ts.setReportFrequencySeconds(reportInterval);
        if (packges != null && !packges.trim().equals(""))
            ts.setMonitoredPackages(packges);
        ts.setSamplingFrequencyMillis(50L);
        ts.setSamplingOutputer(new LoggerSamplingOutputer());
        ts.setActive(true);
        ts.init();
    }

    @Override
    public boolean followsSubject(String subjectType){ return false;}

    @Override
    public void onSubjectUpdate(Data data, String subjectType) { }

    @Override
    public void onTickCallBack() { }

    @Override
    public void onRestate() { }

    @Override
    public void onKill() {
        try {
            if (ts != null)
                ts.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
