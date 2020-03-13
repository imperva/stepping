package com.imperva.stepping;

import java.util.List;

public class SharedDistributionStrategy extends IDistributionStrategy {

    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        //* In this mode the Q is shared by all nodes in the same distributionID.
        //* Sending the event to a random node will enable all the others to access it
        iStepDecorators.get(0).queueSubjectUpdate(data,subjectType);
    }
}

