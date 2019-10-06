package com.imperva.stepping;

public class IdentifiableSteppingException extends SteppingException {
    private final String stepId;

    IdentifiableSteppingException(String stepId, Exception e) {
        super(e);
        this.stepId = stepId;
    }

    IdentifiableSteppingException(String stepId, String message) {
        super(message);
        this.stepId = stepId;
    }

    IdentifiableSteppingException(String stepId, String message, Exception e) {
        super(message, e);
        this.stepId = stepId;
    }

    public String getStepId() {
        return stepId;
    }

}
