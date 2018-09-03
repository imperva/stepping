package Stepping;

import Stepping.container.Container;

public abstract class StepBase extends IStep {
    protected Container container;
    private IMessenger messenger;

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
    public void run() {

    }

}
