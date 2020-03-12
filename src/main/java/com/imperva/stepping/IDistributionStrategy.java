package com.imperva.stepping;

import java.util.List;

public abstract class IDistributionStrategy {
    private static final int MAXIMUM_OFFERS_RETRIES = 10;
    private static final int WAIT_PERIOD_MILLI = 500;

    abstract void distribute(List<IStepDecorator> steps, Data data, String subjectType);

    protected void distribute(Distribution[] distributionList) {
        distribute(distributionList, 1);
    }

    private void distribute(Distribution[] distributionList, int iterationNum) {
        Distribution[] busy = null;

        for (int inc = 0; inc < distributionList.length; inc++) {
            if (distributionList[inc] == null)
                continue;
            if (!distributionList[inc].getiStepDecorator().offerQueueSubjectUpdate(distributionList[inc].getData(), distributionList[inc].getSubject())) {
                if (busy == null) {
                    busy = new Distribution[distributionList.length];
                }
                busy[inc] = distributionList[inc];
            }
        }

        if (!isEmpty(busy)) {
            if (iterationNum > MAXIMUM_OFFERS_RETRIES) {
                for (Distribution dist : distributionList) {
                    if (dist == null)
                        continue;
                    dist.getiStepDecorator().queueSubjectUpdate(dist.getData(), dist.getSubject());
                }
            }
            try {
                System.out.println("Postponing " + iterationNum);
                Thread.sleep(WAIT_PERIOD_MILLI * iterationNum);
            } catch (InterruptedException e) {
                throw new SteppingSystemException("Distribution timeout failed");
            }
            distribute(busy, ++iterationNum);
        }
    }

    private boolean isEmpty(Distribution[] distributions) {
        if (distributions == null)
            return true;
        else {
            for (int inc = 0; inc < distributions.length; inc++) {
                if (distributions[inc] != null) {
                    return false;
                }
            }
        }

        return true;
    }
}
