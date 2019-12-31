package com.imperva.stepping;

public class ContainerService extends ContainerDefaultImpl {

    public RunningScheduled getTickCallbackRunning(String stepID) {
        try {
            String runningID = stepID + ".runningScheduled";
            return ((RunningScheduled) ((Container) getById("__STEPPING_PRIVATE_CONTAINER__")).getById(runningID));
        } catch (Exception e) {
            throw new SteppingException("Running object not found. Please make sure that you enabled TickCallback for this step");
        }
    }

    public int getQSize(String stepID) {
        try {
            String decoratorID = stepID + ".decorator";
            return ((IStepDecorator) ((Container) getById("__STEPPING_PRIVATE_CONTAINER__")).getById(decoratorID)).getQSize();
        } catch (Exception e) {
            throw new SteppingException("Q size not found. Please make sure you use the correct Step ID");
        }
    }
}

