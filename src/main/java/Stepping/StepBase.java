package Stepping;

import Stepping.container.Container;

public abstract class StepBase extends IStep {
    protected Container container;

    protected StepBase(String id, int delay, int initialdelay) {
        super(id, delay, initialdelay);
    }

    @Override
    public void setContainer(Container cntr) {
        container = cntr;
    }
}
