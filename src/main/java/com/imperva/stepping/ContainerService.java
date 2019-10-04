package com.imperva.stepping;

public class ContainerService extends Container {

    public RunningScheduled getTickCallbackRunning(String stepId) {
        try {
            return getById(stepId + ".runningScheduled");
        } catch (Exception e) {
            throw new RuntimeException("Running object not found. Please make sure that you enabled TickCallback for this step");
        }
    }
}
