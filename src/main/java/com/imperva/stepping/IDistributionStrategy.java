package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class IDistributionStrategy {
    private final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);
    private static final int MAXIMUM_OFFERS_RETRIES = 10;
    private static final int WAIT_PERIOD_MILLI = 250;

    protected abstract void distribute(List<IStepDecorator> steps, Data data, String subjectType);

    protected void distribute(Distribution[] distributionList) {
        try {
            distribute(distributionList, 1);
        } catch (Exception ex) {
            logger.error("Distribution failed with Exception: " + ex.toString());
            throw new SteppingSystemException(ex);
        } catch (Error error) {
            logger.error("Distribution failed with Error: " + error.toString());
            throw error;
        }
    }

    private void distribute(Distribution[] distributionList, int iterationNum) {

        Distribution[] busy = null; //*TODO CONSIDER USE LIST WITH INITIAL CAP SIZE

        for (int inc = 0; inc < distributionList.length; inc++) {
            if (distributionList[inc] == null)
                continue;
            boolean isDistributed = distributionList[inc].getiStepDecorator().offerQueueSubjectUpdate(distributionList[inc].getData(), distributionList[inc].getSubject());
            if (!isDistributed) {
                if (busy == null) {
                    busy = new Distribution[distributionList.length];
                }
                busy[inc] = distributionList[inc];
                logger.debug("Distribution not succeeded. Moving to Deceleration Mode for Subject: "  + busy[inc].getSubject() + ". Iteration number - " + iterationNum + "/" + MAXIMUM_OFFERS_RETRIES);
            }
        }


        if (!isEmpty(busy)) {
            //* ***** Deceleration Mode *****
            if (iterationNum >= MAXIMUM_OFFERS_RETRIES) {
                logger.debug("Deceleration Mode failed after " + iterationNum + "/" + MAXIMUM_OFFERS_RETRIES + " retries. Moving back to normal distribution");
                for (Distribution dist : distributionList) {
                    if (dist == null)
                        continue;
                    dist.getiStepDecorator().queueSubjectUpdate(dist.getData(), dist.getSubject());
                }
            }
            try {
                logger.debug("Retarding the distribution for " + WAIT_PERIOD_MILLI * iterationNum + " milliseconds");
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
