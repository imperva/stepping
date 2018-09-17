package Stepping;

import java.util.List;

public abstract class StepBase extends IStep {
    protected Container container;

    private Q q = new Q<ISubject>();

    protected StepBase(String id) {
        super(id);
    }

    @Override
    public void setContainer(Container cntr) {
        container = cntr;
    }


    @Override
    public void newDataArrived(ISubject subject) {
        q.queue(subject);
    }

    @Override
    public void run() {
        List<ISubject> subjectList = q.take();
        if (subjectList.size() > 0) {
            for (ISubject subject : subjectList) {
                newDataArrivedCallBack(subject, container.getById(DefaultID.SUBJECT_CONTAINER.name()));
            }
        } else {
            tickCallBack();
        }
    }

    @Override
    public void close() {
        try {
            shutdown();
        } finally {
            super.close();
        }
    }

    @Override
    public void init() {

    }
}
