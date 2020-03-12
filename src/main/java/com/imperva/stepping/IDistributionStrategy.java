package com.imperva.stepping;

import java.util.List;

public  abstract class IDistributionStrategy {
    //todo StepDecorator should not be exposed to API consumers
   abstract void distribute(List<IStepDecorator> steps, Data data, String subjectType);
   void distribute(Distribution[] distributionList, int iterationNum) {
        Distribution[] busy = null;

        for (int inc = 0; inc < distributionList.length; inc++) {
            if (!distributionList[inc].getiStepDecorator().offerQueueSubjectUpdate(distributionList[inc].getData(), distributionList[inc].getSubject())) {
                if(busy == null)
                    busy = new Distribution[distributionList.length];
                busy[inc] = distributionList[inc];
            }
        }

        if (busy != null) {
            if (iterationNum > 10) {
                for (Distribution dist : distributionList) {
                    dist.getiStepDecorator().queueSubjectUpdate(dist.getData(), dist.getSubject());
                }
            }
            try {
                System.out.println("Postponing "  + iterationNum);
                Thread.sleep(500 * iterationNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            distribute(busy, ++iterationNum);
        }
    }

    void distribute(Distribution[] distributionList) {
        distribute(distributionList, 0);
    }
}
