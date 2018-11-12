package stepping;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container; //todo threadsafe ?
    private Q<Data> q = new Q<>(); //todo threadsafe ?
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
    public void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer,Shutter shutter) {
        step.newDataArrivedCallBack(data, subjectContainer, shutter);
    }

    @Override
    public void newDataArrived(Data data) {
        q.queue(data);
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
                Data data = q.take();
                if (data != null) {
                    newDataArrivedCallBack(data, container.getById(DefaultContainerRegistrarTypes.STEPPING_SUBJECT_CONTAINER.name()),shutter);
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


