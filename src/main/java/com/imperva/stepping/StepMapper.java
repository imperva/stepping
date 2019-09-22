package com.imperva.stepping;

public interface StepMapper extends Step {
    void onSubjectUpdate(Data data, String subjectType, Reducer reducer);
}
