package com.imperva.stepping;

import com.imperva.sampler.ThreadsSampler;
import com.imperva.sampler.outputer.LoggerSamplingOutputer;

import java.io.IOException;

public class PerfSamplerStep implements Step {
    private ThreadsSampler ts;

    @Override
    public void init(Container cntr, Shouter shouter) {
        ts = new ThreadsSampler();
        ts.setMonitoredPackages("com.imperva.algos");
        ts.setReportFrequencySeconds(300);
        ts.setSamplingFrequencyMillis(50L);
        ts.setSamplingOutputer(new LoggerSamplingOutputer());
        ts.setActive(true);
        ts.init();
    }

    @Override
    public void listSubjectsToFollow(Follower follower) {
        //follower.follow(BuiltinSubjectType.STEPPING_PERFSAMPLE.name());
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        sampleNow();
    }

    private void sampleNow() {
       // System.out.println("SAMPLINGGgGGG");
      //  ts.printReport();
    }

    @Override
    public void onTickCallBack() {
        sampleNow();
    }

    @Override
    public void onRestate() {

    }

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
