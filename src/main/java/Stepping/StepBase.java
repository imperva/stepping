package Stepping;

import Stepping.container.Container;

import java.util.List;

public abstract class StepBase extends IStep {
    protected Container container;
    private IMessenger messenger;

    private Q q = new Q<ISubject>();

    protected StepBase(String id) {
        super(id);
    }

    @Override
    public void setContainer(Container cntr) {
        container = cntr;
    }

    @Override
    public void setMessenger(IMessenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void publishData(Data<?> data) {
        messenger.emit(data);
    }

    @Override
    public void dataArrived(ISubject subject) {
        q.queue(subject);
    }

    @Override
    public void run() {
        List<ISubject> x = q.take();
        if (x.size() > 0) {
            System.out.println("@@@@ DATA");
            for (ISubject o : x) {
                start(o, container.getById("subjectContainer"));
            }
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void init() {
        wakenProcessingUnit();
    }
}
