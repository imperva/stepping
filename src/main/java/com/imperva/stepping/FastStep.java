package com.imperva.stepping;

import java.util.concurrent.TimeUnit;

public interface FastStep extends Step {
    default StepConfig getConfig(){
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelay(1);
        stepConfig.setRunningPeriodicDelayUnit(TimeUnit.NANOSECONDS);
        return stepConfig;
    }
}
