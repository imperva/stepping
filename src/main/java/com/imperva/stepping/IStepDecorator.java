package com.imperva.stepping;



import java.io.Closeable;

interface IStepDecorator extends Step, Closeable {

    void init(Container cntr);

    default void onTickCallBack() {
        throw new SteppingException("onTickCallBack not implemented");
    }

    void queueSubjectUpdate(Data data, String subjectType);

    boolean offerQueueSubjectUpdate(Data data, String subjectType);

    Step getStep();

    void setAlgoConfig(AlgoConfig algoConfig);

    void setDistributionNodeID(String name);

    String getDistributionNodeID();

    void openDataSink();

    void attachSubjects();

    Follower listSubjectsToFollow();

    int getQSize();

    int getQCapacity();

    void setQ(Q q);


    Q getQ();
}
