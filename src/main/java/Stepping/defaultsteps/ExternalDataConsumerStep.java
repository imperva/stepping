package Stepping.defaultsteps;

import Stepping.*;
import com.google.gson.JsonObject;

import java.util.List;

public class ExternalDataConsumerStep extends StepBase {

    private IMessenger iMessenger;


    public ExternalDataConsumerStep() {
        super(ExternalDataConsumerStep.class.getName());
    }

    @Override
    public void attach(ISubject iSubject) {

    }

    @Override
    public void shutdown() {
    }

    @Override
    public void init() {

    }

    @Override
    public void restate() {

    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {

    }

    @Override
    protected void tickCallBack() {
        Data data = iMessenger.fetching();
        if (data.getValue() != null) {
            SubjectContainer subjectContainer = container.getById(DefaultID.SUBJECT_CONTAINER.name());
            subjectContainer.<List<JsonObject>>getByName(DefaultSubjectType.S_DATA_ARRIVED.name()).setData(data);
        } else {
            System.out.println("No data received from external resource");
        }
    }

    public void setMessenger(IMessenger iMessenger) {
        this.iMessenger = iMessenger;
    }
}
