package Stepping;

import Stepping.container.Container;

public abstract class IStep extends IRunning {
    protected IStep(String id, int delay, int initialdelay) {
        super(id, delay, initialdelay);
    }

    abstract public void init();
    abstract public void attach(ISubject iSubject);
    abstract public void dataArrived(ISubject iSubject, SubjectContainer subjectContainer);
    abstract public void shutdown();
    abstract public void setContainer(Container cntr);
}
