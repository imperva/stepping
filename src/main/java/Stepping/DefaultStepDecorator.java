package Stepping;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container;
    private IDecelerationStrategy decelerationStrategy;
    private Integer currentDecelerationTimeout = null;

    private Q<ISubject> q = new Q<ISubject>();
    private Step step;

    DefaultStepDecorator(Step step) {
        this.step = step;
    }

    @Override
    public void setContainer(Container cntr) {
        container = cntr;
    }

    @Override
    public StepConfig getStepConfig() {
        return step.getStepConfig();
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
        if(decelerationStrategy == null)
            return 0;
        currentDecelerationTimeout = decelerationStrategy.decelerate(new Date(), queuedItemsSize, this.currentDecelerationTimeout);
        return currentDecelerationTimeout;
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
        if (step.getStepConfig() == null) {
            decelerationStrategy = new DefaultDecelerationStrategy();
            return;
        }

        if (!step.getStepConfig().isEnableDecelerationStrategy()) {
            decelerationStrategy = null;
            return;
        }

        if (step.getStepConfig().getDecelerationStrategy() != null) {
            decelerationStrategy = step.getStepConfig().getDecelerationStrategy();
            return;
        } else {
            decelerationStrategy = new DefaultDecelerationStrategy();
            return;
        }
    }

    @Override
    public boolean isAttach(String subjecType) {
        return step.isAttach(subjecType);
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
    public void run() {
        tickCallBack();
    }
}
