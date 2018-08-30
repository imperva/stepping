package Stepping;

import Stepping.container.Container;

import java.util.ArrayList;
import java.util.List;

public class NewDataArrivedSubject implements ISubject {
    private List<IStep> iSteps = new ArrayList<>();
    private Data data = null;
    public void setData(Data data) {
        this.data = data;
        publish();
    }

    public Data getData() {
        return null;
    }

    public List<IStep> getSubscribers() {
        return null;
    }

    public void attach(IStep step) {
        iSteps.add(step);
    }

    public void publish() {
        for (IStep step : iSteps) {
            step.dataArrived(data, Container.getInstance().getById("subjectContainer"));
        }
    }
}
