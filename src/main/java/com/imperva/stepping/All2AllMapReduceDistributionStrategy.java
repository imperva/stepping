package com.imperva.stepping;

import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;


abstract public  class All2AllMapReduceDistributionStrategy implements IDistributionStrategy {

    public void map(HashMap<IStepDecorator,Data> hashMap, String subjectType) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(hashMap.size());
        for (HashMap.Entry<IStepDecorator,Data> entry : hashMap.entrySet()){
            ((StepMapper)entry.getKey()).onSubjectUpdate(entry.getValue(),subjectType, new Reducer(hashMap.size(), entry.getKey().getConfig().getReducerStep()));
        }
    }
}
