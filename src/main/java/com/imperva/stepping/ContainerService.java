package com.imperva.stepping;

public class ContainerService extends ContainerDefaultImpl {

    static final String RUNNING_SCHEDULED = ".runningScheduled";
    static final String DECORATOR = ".decorator";
    static final String STEPPING_PRIVATE_CONTAINER = "__STEPPING_PRIVATE_CONTAINER__";

    public RunningScheduled getTickCallbackRunning(String stepID) {
        try {
            String runningID = stepID + RUNNING_SCHEDULED;
            return ((RunningScheduled) ((Container) getById(STEPPING_PRIVATE_CONTAINER)).getById(runningID));
        } catch (Exception e) {
            throw new SteppingException("Running object not found. Please make sure that you enabled TickCallback for this step");
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
}

