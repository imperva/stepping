package alogs.etlalgo;

import alogs.Data;
import alogs.IStep;
import alogs.ISubject;

import java.util.List;

public class PreProcessSubject implements ISubject {

    public void occurred(Data data) {

    }

    public Data getData() {
        return null;
    }

    public List<Object> getSubscribers() {
        return null;
    }

    @Override
    public void attach(IStep o) {

    }

    public void publish() {

    }
}
