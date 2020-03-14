package com.imperva.stepping;

import java.util.*;

public class All2AllDistributionStrategy extends IDistributionStrategy {

    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        Distribution[] arr = new Distribution[iStepDecorators.size()];

        for (int inc = 0; inc < iStepDecorators.size(); inc++) {
            arr[inc] = new Distribution(iStepDecorators.get(inc), data, subjectType);
        }

        distribute(arr);
    }
}

