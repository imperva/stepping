package com.imperva.stepping;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedDistributionStrategy extends IDistributionStrategy {

    //* TODO - use super.distribute()?
    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        Data dataCopy = new Data(data.getValue(), data.getSenderId());
        dataCopy.setExpirationCondition((d, c) -> {
            SharedDistributionExpirationContext context = ((SharedDistributionExpirationContext) c);
            return context.getAtomicBarrier().compareAndSet(context.expectedValue, context.newValue);
        }, new SharedDistributionExpirationContext(1, 0));
        for (IStepDecorator step : iStepDecorators) {
            step.queueSubjectUpdate(dataCopy, subjectType);
        }
    }

    class SharedDistributionExpirationContext {
        private AtomicInteger atomicBarrier;
        private int expectedValue;
        private int newValue;

        SharedDistributionExpirationContext(int expectedValue, int newValue) {
            this.atomicBarrier = new AtomicInteger(expectedValue);
            this.expectedValue = expectedValue;
            this.newValue = newValue;
        }

        AtomicInteger getAtomicBarrier() {
            return atomicBarrier;
        }
    }
}

