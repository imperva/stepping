package alogs.etlalgo;

import Stepping.Data;
import Stepping.IStep;
import Stepping.ISubject;

import java.util.List;

public class PreProcessSubject implements ISubject {

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String type) {

    }

    public void setData(Data data) {

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
