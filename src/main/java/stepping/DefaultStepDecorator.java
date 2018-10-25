package stepping;

<<<<<<< HEAD
import java.util.Date;
import java.util.List;

=======
>>>>>>> improve_performance
public class DefaultStepDecorator implements IStepDecorator {
    protected Container container;
    private Q<Data> q = new Q<>();
    private Step step;
    private GlobalAlgoStepConfig globalAlgoStepConfig;
    private StepConfig localStepConfig;
    private String subjectDistributionID = "default";
    private Object tickCallBackDataListenerLocker = new Object();


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
    public void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer) {
        step.newDataArrivedCallBack(data, subjectContainer);
    }

    @Override
    public void newDataArrived(Data data) {
        q.queue(data);
    }

    @Override
    public void tickCallBackThreadSafe() {
        synchronized (tickCallBackDataListenerLocker) {
            tickCallBack();
        }
    }

    @Override
    public void tickCallBack() {
<<<<<<< HEAD
        List<Data> dataList = q.take();
        if (dataList.size() > 0) {
            for (Data data : dataList) {
               newDataArrivedCallBack(data, container.getById(DefaultContainerRegistrarTypes.STEPPING_SUBJECT_CONTAINER.name()));
            }
=======
        try {
            step.tickCallBack();
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultIoCID.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
>>>>>>> improve_performance
        }
    }

    @Override
    public void dataListener() {
        try {
            while (true) {
                Data data = q.take();
                if (data != null) {
                    newDataArrivedCallBack(data, container.getById(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name()));
                }
            }
        } catch (InterruptedException e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultIoCID.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }

    @Override
    public void dataListenerThreadSafe() {
        try {
            while (true) {
                Data data = q.take();

                if (data != null) {
                    synchronized (tickCallBackDataListenerLocker) {
                        newDataArrivedCallBack(data, container.getById(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name()));
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultIoCID.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }

    @Override
    public void close() {
        shuttingDown();
    }

    @Override
    public void init(Container cntr) {
<<<<<<< HEAD
        this.container = cntr;
=======
        //todo why here?
>>>>>>> improve_performance
        int numOfNodes = getLocalStepConfig().getNumOfNodes();
        if (numOfNodes > 0)
            setDistributionNodeID(this.getClass().getName());
<<<<<<< HEAD
        step.init(container);
    }

    private IDecelerationStrategy solveDecelerationStrategy() {
        if (!globalAlgoStepConfig.isEnableDecelerationStrategy() || !getLocalStepConfig().isEnableDecelerationStrategy()) {
            return null;
        }

        if (globalAlgoStepConfig.getDecelerationStrategy() != null) {
            return globalAlgoStepConfig.getDecelerationStrategy();
        } else {
            return new DefaultLeakyBucketDecelerationStrategy();
        }
=======
        step.init(cntr);
        this.container = cntr;
>>>>>>> improve_performance
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
<<<<<<< HEAD
    public void run() {
        try {
            tickCallBack();
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultContainerRegistrarTypes.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
            throw e;
        }
    }

    @Override
    public StepConfig getLocalStepConfig(){
=======
    public StepConfig getLocalStepConfig() {
>>>>>>> improve_performance
        localStepConfig = step.getLocalStepConfig();
        if (localStepConfig == null)
            throw new RuntimeException("Is required");
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

//        int size = dataList.stream().mapToInt((data) -> data.getSize()).sum();
//        decelerate(calcDecelerationTimeout(size));

//    private void decelerate(int decelerationTimeout) {
//        try {
//            if (decelerationTimeout > 0)
//                Thread.sleep(decelerationTimeout);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

//    private int calcDecelerationTimeout(int queuedItemsSize) {
//        IDecelerationStrategy decelerationStrategy = solveDecelerationStrategy();
//        if (decelerationStrategy == null)
//            return 0;
//        Date now = new Date();
//        this.currentDecelerationTimeout = decelerationStrategy.decelerate(now, queuedItemsSize, this.currentDecelerationTimeout);
//        if (currentDecelerationTimeout > 0)
//            System.out.println(this.step.getClass().getName() + " calcDecelerationTimeout: " + currentDecelerationTimeout);
//        return this.currentDecelerationTimeout;
//    }

//    private IDecelerationStrategy solveDecelerationStrategy() {
//        if (!globalAlgoStepConfig.isEnableDecelerationStrategy() || !getLocalStepConfig().isEnableDecelerationStrategy()) {
//            return null;
//        }
//
//        if (globalAlgoStepConfig.getDecelerationStrategy() != null) {
//            return globalAlgoStepConfig.getDecelerationStrategy();
//        } else {
//            return new DefaultLeakyBucketDecelerationStrategy();
//        }
//     }

