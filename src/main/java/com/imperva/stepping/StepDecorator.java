package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class StepDecorator implements IStepDecorator {
    static final Logger logger = LoggerFactory.getLogger(StepDecorator.class);
    protected Container container;
    private Q<Message> q = new Q<>();
    private Step step;
    private AlgoConfig algoConfig;
    private StepConfig localStepConfig;
    private String subjectDistributionID = "default";
    private volatile boolean  dead = false;
    private Follower follower = null;


    StepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void init(Container cntr) {
        init(cntr, cntr.getById(BuiltinTypes.STEPPING_SHOUTER.name()));
    }

    @Override
    public void init(Container cntr, Shouter shouter) {
        logger.debug("Initializing Step - " + getStep().getClass().getName());
        container = cntr;
        step.init(container, shouter);
    }

    @Override
    public void onRestate() {
        logger.info("Start Restate phase for Step - " + getStep().getClass().getName());
        step.onRestate();
    }

    @Override
    public void onKill() {
        step.onKill();
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        step.onSubjectUpdate(data, subjectType);
    }

    @Override
    public void queueSubjectUpdate(Data data, String subjectType) {
        q.queue(new Message(data, subjectType));
    }

    @Override
    public void onTickCallBack() {
        try {
            step.onTickCallBack();
        } catch (Exception e) {
            throw new SteppingException(getStep().getClass().getName(), "onTickCallback FAILED", e);
        }
    }

    @Override
    public void openDataSink() {
        try {
            logger.info("Opening DataSink for Step - " + getStep().getClass().getName());
            while (!dead) {
                if (Thread.currentThread().isInterrupted())
                    throw new InterruptedException();
                Message message = q.take();

                if (message.getSubjectType().equals("POISON-PILL")) {
                    logger.info("Taking a Poison Pill. " + getStep().getClass().getName() + " is going to die");
                    Thread.currentThread().interrupt();
                    dead = true;
                    logger.info("I am dead - " + getStep().getClass().getName());
                    throw new InterruptedException();
                }

                if (message != null && message.getData() != null) {
                    if (!message.getSubjectType().equals(BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name())) {
                        onSubjectUpdate(message.getData(), message.getSubjectType());
                    } else {
                        onTickCallBack();
                        CyclicBarrier cb = (CyclicBarrier) message.getData().getValue();
                        cb.await();
                    }
                }
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new SteppingSystemException(e);
        } catch (Exception e) {
            throw new SteppingException(getStep().getClass().getName(), "DataSink FAILED", e);
        }
    }

    @Override
    public void attachSubjects() {
        Follower follower = listSubjectsToFollow();
        if (follower != null && follower.size() != 0) {
            for (String subjectType : follower.get()) {
                ISubject s = container.getById(subjectType);
                if (s == null) {
                   throw new RuntimeException("Can't attach null Subject to be followed");
                }
                s.attach(this);
            }
        } else {
            List<ISubject> subjects = container.getSonOf(ISubject.class);
            for (ISubject subject : subjects) {
                followSubject(subject);
            }
        }
    }

    @Override
    public boolean followsSubject(String subjectType) {
        boolean isFollowSubject = step.followsSubject(subjectType);
        return isFollowSubject;
    }

    @Override
    public void listSubjectsToFollow(Follower follower) {
        step.listSubjectsToFollow(follower);
    }

    private void followSubject(ISubject iSubject) {
        try {
            boolean isAttached = followsSubject(iSubject.getType());
            if (isAttached)
                iSubject.attach(this);
        } catch (Exception e) {
            throw new SteppingException(getStep().getClass().getName(), "followSubject registration FAILED", e);
        }
    }

    @Override
    public Follower listSubjectsToFollow() {
        if(follower != null)
            return follower;
        Follower follower = new Follower();
        listSubjectsToFollow(follower);
        this.follower = new Follower(follower);
        return follower;
    }

    @Override
    public Step getStep() {
        return step;
    }

    @Override
    public void setAlgoConfig(AlgoConfig algoConfig) {
        if (algoConfig == null)
            throw new RuntimeException("AlgoConfig is required");
        this.algoConfig = algoConfig;
    }

    @Override
    public StepConfig getConfig() {
        localStepConfig = step.getConfig();
        if (localStepConfig == null)
            throw new RuntimeException("LocalStepConfig is required");
        return localStepConfig;
    }

    @Override
    public void setDistributionNodeID(String name) {
        this.subjectDistributionID = name;
    }

    @Override
    public String getDistributionNodeID() {
        return subjectDistributionID;
    }

    @Override
    public void close() {
        logger.info("Forwarding Kill handling to Step impl- " + getStep().getClass().getName());
        onKill();
    }
}


