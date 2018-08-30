package Stepping;

import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

public class Subject<T> implements ISubject<T> {
    private String type;
    public Subject(String type){
        this.type = type;
    }
    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String type) {

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

    }

    @Override
    public void attach(IStep o) {

    }

    @Override
    public void setData(Data<T> data) {

    }
}
