package stepping;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container;
    private Q<Message> q = new Q<>();
    private Step step;
    private AlgoConfig algoConfig;
    private StepConfig localStepConfig;
    private String subjectDistributionID = "default";


    DefaultStepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void onRestate() {
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
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(BuiltinTypes.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }

    @Override
    public void openDataSink() {
        try {
            while (true) {
                Message message = q.take();
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
            System.out.println("EXCEPTION");
            container.<IExceptionHandler>getById(BuiltinTypes.STEPPING_EXCEPTION_HANDLER.name()).handle(e);
        }
    }


    @Override
    public void close() {
        onKill();
    }

    @Override
    public void init(Container cntr) {
        init(cntr, cntr.getById(BuiltinTypes.STEPPING_SHOUTER.name()));
    }

    @Override
    public void init(Container cntr, Shouter shouter) {
        container = cntr;
        step.init(container, shouter);
    }

    @Override
    public boolean followsSubject(String subjectType) {
        return step.followsSubject(subjectType);
    }

    @Override
    public void followSubject(ISubject iSubject) {
        boolean isAttached = followsSubject(iSubject.getType());
        if (isAttached)
            iSubject.attach(this);
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
}


