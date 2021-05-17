package com.imperva.stepping;



import java.io.Closeable;

interface IStepDecorator extends Step, Closeable {

    void init(Container cntr, Shouter shouter);

    default void onTickCallBack() {
        throw new SteppingException("onTickCallBack not implemented");
    }

    void queueSubjectUpdate(Data data, String subjectType);

    boolean offerQueueSubjectUpdate(Data data, String subjectType);

    void clearQueueSubject();

    Step getStep();

    void setDistributionNodeID(String name);

    String getDistributionNodeID();

    void openDataSink();

    void attachSubjects();

    Follower listSubjectsToFollow();

    int getQSize();

    int getQCapacity();

    IDistributionStrategy getDistributionStrategy(String subjectType);
}
