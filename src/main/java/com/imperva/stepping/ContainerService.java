package com.imperva.stepping;

public class ContainerService extends ContainerDefaultImpl {

    public RunningScheduled getTickCallbackRunning(String stepID) {
        try {
            return getById(stepID + ".runningScheduled");
        } catch (Exception e) {
            throw new SteppingException("Running object not found. Please make sure that you enabled TickCallback for this step");
        }
    }
}

