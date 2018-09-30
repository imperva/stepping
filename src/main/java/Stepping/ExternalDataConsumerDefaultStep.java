package Stepping;

import com.google.gson.JsonObject;

import java.util.List;

public class ExternalDataConsumerDefaultStep implements Step {

    private IMessenger iMessenger;
    private Container container;


    public ExternalDataConsumerDefaultStep() {
       // super(ExternalDataConsumerDefaultStep.class.getName());
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjectType) {
        return false;
    }

    @Override
    public void restate() {

    }

    @Override
    public void shuttingDown() {

    }

    @Override
    public void setContainer(Container cntr) {
        this.container = cntr;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {

    }

    @Override
    public void tickCallBack() {
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