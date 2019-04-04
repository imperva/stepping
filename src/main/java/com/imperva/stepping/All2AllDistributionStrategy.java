package com.imperva.stepping;

import java.util.List;

public class All2AllDistributionStrategy implements IDistributionStrategy {
    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        for (IStepDecorator step : iStepDecorators) {
            step.queueSubjectUpdate(data, subjectType);
        }
    }
}