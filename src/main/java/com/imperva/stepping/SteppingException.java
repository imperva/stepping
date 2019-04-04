package com.imperva.stepping;

public class SteppingException extends RuntimeException {
    private final String stepId;

    SteppingException(String stepId, Exception e) {
        super(e);
        this.stepId = stepId;
    }

    SteppingException(String stepId, String message, Exception e) {
        super(message, e);
        this.stepId = stepId;
    }

    public String getStepId() {
        return stepId;
    }
}
