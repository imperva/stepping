package Stepping;

public abstract class IStep extends IRunning {
    protected IStep(String id) {
        super(id);
    }

    protected IStep(String id, int delay, int initialdelay, boolean daemon) {
        super(id, delay, initialdelay, daemon);
    }

    abstract public void init();

    abstract public void attach(ISubject iSubject);

    abstract public void newDataArrived(ISubject iSubject);

    abstract public void shutdown();

    abstract public void setContainer(Container cntr);

    abstract protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer);

    abstract protected void tickCallBack();

    abstract public void restate();
}
