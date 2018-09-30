package Stepping;
import java.util.Date;
import java.util.List;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container;
    private int currentDecelerationTimeout = 0;

    private Q<ISubject> q = new Q<ISubject>();
    private Step step;
    private StepConfig globalAlgoStepConfig;
    private StepConfig localStepConfig;

    DefaultStepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void setContainer(Container cntr) {
        container = cntr;
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
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        step.newDataArrivedCallBack(subject, subjectContainer);
    }

    @Override
    public void newDataArrived(ISubject subject) {
        q.queue(subject);
    }

    @Override
    public void tickCallBack() {
        List<ISubject> subjectList = q.take();
        if (subjectList.size() > 0) {
            for (ISubject subject : subjectList) {
               newDataArrivedCallBack(subject, container.getById(DefaultID.SUBJECT_CONTAINER.name()));
            }
        }
        decelerate(calcDecelerationTimeout(subjectList.size()));
        step.tickCallBack();
    }

    private void decelerate(int decelerationTimeout) {
        try {
            if (decelerationTimeout > 0)
                Thread.sleep(decelerationTimeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int calcDecelerationTimeout(int queuedItemsSize) {
        IDecelerationStrategy decelerationStrategy = solveDecelerationStrategy(solveStepConfig());
        if (decelerationStrategy == null)
            return 0;
        Date now = new Date();
        this.currentDecelerationTimeout = decelerationStrategy.decelerate(now, queuedItemsSize, this.currentDecelerationTimeout);
        return this.currentDecelerationTimeout;
    }

    private StepConfig solveStepConfig() {
        if (localStepConfig == null) {
            System.out.println("solveStepConfig ****" + this.step.getClass().getName());
            StepConfig localConfig = getLocalStepConfig();
            localStepConfig = localConfig != null ? localConfig : globalAlgoStepConfig;
        }
        return localStepConfig;
    }

    @Override
    public void close() {
        shuttingDown();
    }

    @Override
    public void init() {
        step.init();
    }

    private IDecelerationStrategy solveDecelerationStrategy(StepConfig stepConfig) {
        if (stepConfig == null) {
            return new DefaultDecelerationStrategy();
        }

        if (!stepConfig.isEnableDecelerationStrategy()) {
            return null;
        }

        if (stepConfig.getDecelerationStrategy() != null) {
            return stepConfig.getDecelerationStrategy();
        } else {
            return new DefaultDecelerationStrategy();
        }
    }

    @Override
    public boolean isAttach(String subjectType) {
        return step.isAttach(subjectType);
    }

    @Override
    public void attach(ISubject iSubject) {
        boolean isAttached = isAttach(iSubject.getType());
        if (isAttached)
            iSubject.attach(this);
    }

    @Override
    public Step getStep() {
        return step;
    }

    @Override
    public void setGlobalAlgoStepConfig(StepConfig globalAlgoStepConfig) {
        if(globalAlgoStepConfig == null)
            throw new RuntimeException("GlobalAlgoStepConfig is required");
        this.globalAlgoStepConfig = globalAlgoStepConfig;

    }

    @Override
    public void run() {
        try {
            tickCallBack();
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(DefaultID.EXCEPTION_HANDLER.name()).handle(e);
            throw e;
        }
    }

    @Override
    public StepConfig getLocalStepConfig(){
        return step.getLocalStepConfig();
    }
}

