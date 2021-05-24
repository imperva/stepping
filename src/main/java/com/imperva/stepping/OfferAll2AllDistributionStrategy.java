package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OfferAll2AllDistributionStrategy extends IDistributionStrategy {
    private final Logger logger = LoggerFactory.getLogger(OfferAll2AllDistributionStrategy.class);

    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        iStepDecorators.forEach(iStepDecorator -> {
            boolean isDistributed = iStepDecorator.offerQueueSubjectUpdate(data, subjectType);
            if (!isDistributed)
                logger.error("Failed to distribute Subject:" + subjectType);
        });
    }
}

