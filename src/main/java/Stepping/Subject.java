package Stepping;

import Stepping.container.Container;

import java.util.List;

public class Subject<T> implements ISubject<T> {

    private String type;

    public Subject() { }
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
        return null;
    }

    @Override
    public List<IStep> getSubscribers() {
        return null;
    }

    @Override
    public void publish() {
        List<IStep> iSteps = Container.getInstance().getTypeOf(IStep.class);
        for (IStep step : iSteps) {
            step.dataArrived(this, Container.getInstance().getById("subjectContainer"));
        }
    }

    @Override
    public void attach(IStep o) {

    }

    @Override
    public void setData(Data<T> data) {

    }
}
