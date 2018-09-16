package Stepping.defaultsteps;

import Stepping.*;

public class ExternalDataConsumerStep extends StepBase {

    private IMessenger iMessenger;
    private IExternalDataReceiver externalDataReceiver;

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

            //There are two internal options to start the algo: 1. Start from the externalDataReceiver (the algo itself) or from initial step that attached to S_DATA_ARRIVED
            if (externalDataReceiver != null) {
                //Fire externalDataReceiver if we want to use orchestrator to manage the flow
                externalDataReceiver.newDataArrived(data);
            } else {
                SubjectContainer subjectContainer = container.getById(DefaultID.SUBJECT_CONTAINER.name());
                subjectContainer.getByName(DefaultSubjectType.S_DATA_ARRIVED.name()).setData(data);
            }
        } else {
            System.out.println("No data received from external resource");
        }
    }

    @Override
    public void setMessenger(IMessenger iMessenger) {
        this.iMessenger = iMessenger;
    }

    public void setExternalDataReceiver(IExternalDataReceiver externalDataReceiver) {
        this.externalDataReceiver = externalDataReceiver;
    }
}
