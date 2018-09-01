package Stepping;

import Stepping.container.ContainerSingleton;

import java.util.ArrayList;
import java.util.List;

public class Subject<T> implements ISubject<T> {

    private List<IStep> iSteps = new ArrayList<IStep>();
    private String type;
    private Data<T> data;

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
        Thread.currentThread().getThreadGroup().getParent();
        for (IStep step : getSubscribers()) {
            step.dataArrived(this, ContainerSingleton.getInstance().getById("subjectContainer"));
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
}
