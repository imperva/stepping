package com.imperva.stepping;

public class IdentifiableSteppingError extends SteppingError {
    private final String stepId;

    IdentifiableSteppingError(String stepId, Error e) {
        super(e);
        this.stepId = stepId;
    }

    IdentifiableSteppingError(String stepId, String message) {
        super(message);
        this.stepId = stepId;
    }

    IdentifiableSteppingError(String stepId, String message, Error e) {
        super(message, e);
        this.stepId = stepId;
    }

    public String getStepId() {
        return stepId;
    }

}
