package com.imperva.stepping;

import java.util.concurrent.TimeUnit;

public class ContainerService extends ContainerDefaultImpl {

    static final String RUNNING_SCHEDULED = ".runningScheduled";
    static final String DECORATOR = ".decorator";
    static final String STEPPING_PRIVATE_CONTAINER = "__STEPPING_PRIVATE_CONTAINER__";

    public void changeDelay(String stepID, int delay, int initialDelay, TimeUnit timeUnit) {
        try {
            String runningID = stepID + RUNNING_SCHEDULED;
            ((RunningScheduled) ((Container) getById(STEPPING_PRIVATE_CONTAINER)).getById(runningID)).changeDelay(delay, initialDelay, timeUnit);
        } catch (Exception e) {
            throw new SteppingException("Change Delay Failed. Please make sure that you enabled TickCallback for this step");
        }
    }

    public int getQSize(String stepID) {
        try {
            String decoratorID = stepID + DECORATOR;
            return ((IStepDecorator) ((Container) getById(STEPPING_PRIVATE_CONTAINER)).getById(decoratorID)).getQSize();
        } catch (Exception e) {
            throw new SteppingException("Q size not found. Please make sure you use the correct Step ID");
        }
    }

    //* FOR INTERNAL USAGE ONLY - cron expression must be re-evaluated every tickcallback, this is possible only with StepConfig API
    void changeDelay(String stepID, String cron) {
        try {
            String runningID = stepID + RUNNING_SCHEDULED;
            ((RunningScheduled) ((Container) getById(STEPPING_PRIVATE_CONTAINER)).getById(runningID)).changeDelay(cron);
        } catch (Exception e) {
            throw new SteppingException("Change Delay Failed. Please make sure that you enabled TickCallback for this step");
        }
    }

}

