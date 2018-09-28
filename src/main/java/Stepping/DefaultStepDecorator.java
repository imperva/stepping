package Stepping;
import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DefaultStepDecorator implements IStepDecorator {
    private final IAlgoDecorator algoDecorator;
    protected Container container;
    private IDecelerationStrategy decelerationStrategy;
    private int currentDecelerationTimeout = 0;

    private Q<ISubject> q = new Q<ISubject>();
    private Step step;
    private StepConfig stepConfig;

    DefaultStepDecorator(Step step, IAlgoDecorator algoDecorator) {
        this.algoDecorator = algoDecorator;
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
        step.tickCallBack();

        decelerate(calcDecelerationTimeout(subjectList.size()));
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
        if (this.decelerationStrategy == null)
            return 0;
        Date now = new Date();
        this.currentDecelerationTimeout = decelerationStrategy.decelerate(now, queuedItemsSize, this.currentDecelerationTimeout);
        return this.currentDecelerationTimeout;
    }

    @Override
    public void close() {
        shuttingDown();
    }

    @Override
    public void init() {
        step.init();
        setDecelerationStrategy();
    }

    private void setDecelerationStrategy() {
        if (stepConfig == null) {
            decelerationStrategy = new DefaultDecelerationStrategy();
            return;
        }

        if (!stepConfig.isEnableDecelerationStrategy()) {
            decelerationStrategy = null;
            return;
        }

        if (stepConfig.getDecelerationStrategy() != null) {
            decelerationStrategy = stepConfig.getDecelerationStrategy();
            return;
        } else {
            decelerationStrategy = new DefaultDecelerationStrategy();
            return;
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
    public void setStepConfig(StepConfig stepConfig) {
        this.stepConfig = stepConfig;

    }

    @Override
    public void run() {
        try {
            tickCallBack();
        } catch (Exception e) {
            System.out.println("EXCEPTION");

            close();
            try {
                algoDecorator.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            throw e;

        }
    }
}

