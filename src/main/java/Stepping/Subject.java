package Stepping;

import Stepping.container.Container;
import java.util.ArrayList;
import java.util.List;

public class Subject<T> implements ISubject<T> {

    private List<IStep> iSteps = new ArrayList<IStep>();
    private String type;
    private Data<T> data;
    private Container cntr;

    public Subject() {
    }

    public Subject(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Data<T> getData() {
        return data;
    }

    @Override
    public List<IStep> getSubscribers() {
        return iSteps;
    }

    @Override
    public void publish() {

        for (IStep step : getSubscribers()) {
            step.dataArrived(this);
        }
    }

    @Override
    public void attach(IStep step) {
        iSteps.add(step);
    }

    @Override
    public void setData(Data<T> data) {
        this.data = data;
        publish();
    }

    @Override
    public Container getContainer() {
        return cntr;
    }

    @Override
    public void setContainer(Container container) {
        this.cntr = container;
    }
}
