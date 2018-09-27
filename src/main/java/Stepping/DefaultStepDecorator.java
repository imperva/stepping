package Stepping;

import java.io.IOException;
import java.util.List;

public class DefaultStepDecorator implements IStepDecorator {
    protected Container container;

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
    public void restate() {
        step.restate();
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
    }

    @Override
    public void close() {
        try {
            step.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close();
        }
    }

    @Override
    public void init() {
        step.init();
    }

    @Override
    public boolean isAttach(String eventType) {
        return step.isAttach(eventType);
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
