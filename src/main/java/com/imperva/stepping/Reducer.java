package com.imperva.stepping;

import java.util.concurrent.CyclicBarrier;

public class Reducer {

    private CyclicBarrier cyclicBarrier;
    private Step stepReducer;


    Reducer(int numOfNodes, Step stepReducer){
        this.cyclicBarrier = new CyclicBarrier(numOfNodes);
        this.stepReducer = stepReducer;
    }

    public void reduce(Data data, String subject)  {
        data.addMetadata("synchronyzer",cyclicBarrier);
        data.addMetadata("numOfNodes",cyclicBarrier.getParties());
        stepReducer.onSubjectUpdate(data, BuiltinSubjectType.STEPPING_REDUCE_EVENT + ":" + subject);
    }
}
