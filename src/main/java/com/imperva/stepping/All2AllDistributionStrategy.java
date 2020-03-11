package com.imperva.stepping;

import java.util.*;

public class All2AllDistributionStrategy implements IDistributionStrategy {
    HashMap<List<IStepDecorator>, Message> distributionList = new HashMap<>();

    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
//        for (IStepDecorator step : iStepDecorators) {
//            step.queueSubjectUpdate(data, subjectType);
//        }

        dodo(iStepDecorators, data, subjectType);
    }


    void dodo(List<IStepDecorator> distributionList, Data data, String subjectType) {
        List<IStepDecorator> busy = new ArrayList<>();

        for (IStepDecorator stepDecorator : distributionList) {
            if (!stepDecorator.offerQueueSubjectUpdate(data, subjectType)) {
                busy.add(stepDecorator);
            }
        }

        if (busy.size() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dodo(busy, data, subjectType);
        }
    }
}

