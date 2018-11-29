package stepping;

import java.util.concurrent.CountDownLatch;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container; //todo threadsafe ?
    private Q<Message> q = new Q<>(); //todo threadsafe ?
    private Step step;
    private GlobalAlgoStepConfig globalAlgoStepConfig;
    private StepConfig localStepConfig;
    private String subjectDistributionID = "default";
    private Shutter shutter = new Shutter();


    DefaultStepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void restate() {
        step.restate();
    }

    @Override
    public void shuttingDown() {
        step.shuttingDown();
    }

    @Override
    public void newDataArrivedCallBack(Data data, String subjectType, SubjectContainer subjectContainer,Shutter shutter) {
        step.newDataArrivedCallBack(data, subjectType, subjectContainer, shutter);
    }

    @Override
    public void newDataArrived(Data data, String subjectType) {
        q.queue(new Message(data,subjectType));
    }

    @Override
    public void tickCallBack() {
        tickCallBack(shutter);
    }

    @Override
    public void tickCallBack(Shutter shutter) {
        try {
            step.tickCallBack(shutter);
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultContainerRegistrarTypes.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }

    @Override
    public void dataListener() {
        try {
            while (true) {
                Message message = q.take();
                if (message != null && message.getData() != null) {
                    if (!message.getSubjectType().equals("Timeout")) {
                        newDataArrivedCallBack(message.getData(), message.getSubjectType(), container.getById(DefaultContainerRegistrarTypes.STEPPING_SUBJECT_CONTAINER.name()), shutter);
                    } else {
                        tickCallBack();
                      // CountDownLatch countDownLatch =  (CountDownLatch) message.getData().getValue();
                      // countDownLatch.countDown();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultContainerRegistrarTypes.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }


    @Override
    public void close() {
        shuttingDown();
    }

    @Override
    public void init(Container cntr) {
        this.container = cntr;
        //todo why here?
        int numOfNodes = getLocalStepConfig().getNumOfNodes();
        if (numOfNodes > 0)
            setDistributionNodeID(this.getClass().getName());
        step.init(container);
    }


    @Override
    public boolean followsSubject(String subjectType) {
        return step.followsSubject(subjectType);
    }

    @Override
    public void attach(ISubject iSubject) {
        boolean isAttached = followsSubject(iSubject.getType());
        if (isAttached)
            iSubject.attach(this);
    }

    @Override
    public Step getStep() {
        return step;
    }

    @Override
    public void setGlobalAlgoStepConfig(GlobalAlgoStepConfig globalAlgoStepConfig) {
        if (globalAlgoStepConfig == null)
            throw new RuntimeException("GlobalAlgoStepConfig is required");
        this.globalAlgoStepConfig = globalAlgoStepConfig;

    }

    @Override
    public StepConfig getLocalStepConfig(){
        localStepConfig = step.getLocalStepConfig();
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


}


