package com.imperva.stepping;

public class ContainerService extends Container {
    public RunningScheduled getTickCallbackRunning(String stepId){
       return getById(stepId + ".runningScheduled");
    }
}
