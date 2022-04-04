package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class StepDecorator implements IStepDecorator {
    private final Logger logger = LoggerFactory.getLogger(StepDecorator.class);
    protected Container container;
    Q<Message> q;
    private Step step;
    private StepConfig localStepConfig;
    private String subjectDistributionID = "default";
    volatile boolean dead = false;
    private Follower follower;
    private CyclicBarrier cb;
    private String id;
    private Shouter shouter;
    private HashMap<String, SubjectUpdateEvent> subjectUpdateEvents = new HashMap<>();
    private boolean isSystemStep;
    private Boolean isMonitorEnabledForStep;
    private MonitorAgent monitorAgent;


    StepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void init(Container cntr, Shouter shouter) {
        logger.debug("Initializing Step - " + getStep().getId());
        container = cntr;
        step.init(container, shouter);
        q = new Q<>(getConfig().getBoundQueueCapacity());
        this.shouter = shouter;
        isSystemStep = isSystemStep();
        isMonitorEnabledForStep = localStepConfig.getIsMonitorEnabledForStep();
        if (isMonitorEnabledForStep)
            monitorAgent = new MonitorAgent(shouter, getConfig().getMonitorEmmitTimeout());
    }

    @Override
    public void onRestate() {
        logger.info("Start Restate phase for Step - " + getStep().getId());
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
        if (StringUtils.isEmpty(subjectType) || data == null)
            throw new SteppingException("Can't queue an empty Subject or empty Data");
        q.queue(new Message(data, subjectType));
    }

    @Override
    public boolean offerQueueSubjectUpdate(Data data, String subjectType) {
        if (StringUtils.isEmpty(subjectType) || data == null)
            throw new SteppingException("Can't offer an empty Subject or empty Data");
        return q.offer(new Message(data, subjectType));
    }

    @Override
    public void clearQueueSubject() {
        q.clear();
    }

    @Override
    public void onTickCallBack() {
        try {
            step.onTickCallBack();
        } catch (Exception e) {
            throw new IdentifiableSteppingException(getStep().getId(), "onTickCallback FAILED", e);
        }
    }

    @Override
    public void openDataSink() {
        try {
            logger.info("Opening DataSink for Step - " + getStep().getId());
            setThreadName();
            while (!dead) {
                if (Thread.currentThread().isInterrupted())
                    throw new InterruptedException();
                Message message = q.take();

                boolean isTickCallBack = message.getSubjectType().equals(BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());

                if (message.getData().isExpirable()) {
                    boolean succeeded = message.getData().tryGrabAndExpire();
                    if (!succeeded) {
                        continue;
                    }
                }

                if (isMonitorEnabledForStep && !isSystemStep) {
                    monitorAgent.start(message.getData().getSize(), q.size());
                }

                if (message.getSubjectType().equals("POISON-PILL")) {
                    logger.info("Taking a Poison Pill. " + getStep().getId() + " is going to die");
                    dead = true;
                    logger.info("I am dead - " + getStep().getId());
                    throw new InterruptedException();
                }

                if (!isTickCallBack) {
                    SubjectUpdateEvent subjectUpdateEvent = subjectUpdateEvents.get(message.getSubjectType());
                    if (subjectUpdateEvent != null)
                        subjectUpdateEvent.onUpdate(message.getData());

                    onSubjectUpdate(message.getData(), message.getSubjectType());
                } else {
                    try {
                        onTickCallBack();
                        if (getConfig().getRunningPeriodicCronDelay() != null) {
                            try {
                                changeTickCallBackDelay(getConfig().getRunningPeriodicCronDelay());
                            } catch (Exception x) {
                                throw new SteppingException(x.toString());
                            }
                        }
                    } finally {
                        cb = (CyclicBarrier) message.getData().getValue();
                        cb.await();
                    }
                }
                if (isMonitorEnabledForStep && !isSystemStep) {
                    monitorAgent.stop();
                }
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new SteppingSystemException(e);
        } catch (Exception e) {
            throw new IdentifiableSteppingException(getStep().getId(), "DataSink FAILED", e);
        } catch (Error err) {
            throw new IdentifiableSteppingError(getStep().getId(), "DataSink FAILED - ERROR", err);
        }
    }

    private void changeTickCallBackDelay(String cronExpression) {
      ((ContainerService) container).changeDelay(getStep().getId(), cronExpression);
    }

    private void setThreadName() {
        if (! step.getConfig().isKeepOriginalThreadName())
            Thread.currentThread().setName(getId() + ".running");
    }

    @Override
    public void attachSubjects() {
        Follower follower = listSubjectsToFollow();
        if (follower.size() != 0) {
            for (FollowRequest followRequest : follower.get()) {
                ISubject s = container.getById(followRequest.getSubjectType());
                if (s == null) {
                    throw new SteppingSystemException("Can't attach null Subject to be followed. Step id: " + this.step.getId());
                }
                s.attach(this);


                if (followRequest.getSubjectUpdateEvent() != null)
                    subjectUpdateEvents.put(followRequest.getSubjectType(), followRequest.getSubjectUpdateEvent());
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
            throw new IdentifiableSteppingException(getStep().getId(), "followSubject registration FAILED", e);
        }
    }

    @Override
    public Follower listSubjectsToFollow() {
        if (follower != null)
            return follower;
        Follower follower = new Follower();
        listSubjectsToFollow(follower);
        this.follower = new Follower(follower);
        return follower;
    }

    @Override
    public int getQSize() {
        return q.size();
    }

    @Override
    public int getQCapacity() {
        return q.getCapacity();
    }

    @Override
    public IDistributionStrategy getDistributionStrategy(String subjectType) {

        IDistributionStrategy stepConfigDistributionStrategy = getConfig().getDistributionStrategy();

        Optional<FollowRequest> followRequestData = listSubjectsToFollow().getFollowRequest(subjectType);

        if ((!followRequestData.isPresent() || followRequestData.get().getDistributionStrategy() == null) && stepConfigDistributionStrategy == null)
            throw new SteppingException("Distribution Strategy for Step " + step.getId() + " is missing.");

        if (followRequestData.isPresent() && followRequestData.get().getDistributionStrategy() != null) {
            return followRequestData.get().getDistributionStrategy();
        }
        return stepConfigDistributionStrategy;

    }

    @Override
    public Step getStep() {
        return step;
    }

    @Override
    public StepConfig getConfig() {
        if (localStepConfig != null)
            return localStepConfig;
        localStepConfig = step.getConfig();
        if (localStepConfig == null)
            throw new IdentifiableSteppingException(this.step.getId(), "StepConfig is required");
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
        logger.info("Forwarding Kill handling to Step - " + getStep().getId());
        onKill();
        if (cb != null)
            cb.reset();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public HashMap<String, SubjectUpdateEvent> getSubjectUpdateEvents(){
        return subjectUpdateEvents;
    }

    @Override
    public boolean isSystemStep() {
        return getStep().getClass().isAnnotationPresent(SystemStep.class);
    }
}


