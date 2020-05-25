package com.imperva.stepping;

import java.util.List;

public class SharedDistributionStrategy extends IDistributionStrategy {
    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        data.setExpirationCondition(1,0);
        for (IStepDecorator step : iStepDecorators) {
            step.queueSubjectUpdate(data, subjectType);
        }
    }
}

