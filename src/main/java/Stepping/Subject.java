package Stepping;

import java.util.ArrayList;
import java.util.List;

public class Subject implements ISubject {

    private List<IStep> iSteps = new ArrayList<IStep>();
    private String type;
    private Data data;
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
    public Data getData() {
        return data;
    }

    @Override
    public List<IStep> getSubscribers() {
        return iSteps;
    }

    @Override
    public void publish() {

        for (IStep step : getSubscribers()) {
            step.newDataArrived(this);
        }
    }

    @Override
    public void attach(IStep step) {
        iSteps.add(step);
    }

    @Override
    public void setData(Data data) {
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
