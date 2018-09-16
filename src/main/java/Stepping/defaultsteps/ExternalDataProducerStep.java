package Stepping.defaultsteps;

import Stepping.IMessenger;
import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;

public class ExternalDataProducerStep extends StepBase {

    private IMessenger iMessenger;

    public ExternalDataProducerStep() {
        super(ExternalDataProducerStep.class.getName());
    }

    @Override
    public void attach(ISubject iSubject) {
        if (DefaultSubjectType.S_PUBLISH_DATA.name().equals(iSubject.getType())) {
            iSubject.attach(this);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.S_PUBLISH_DATA.name().equals(subject.getType())) {
            iMessenger.emit(subject.getData());
        }
    }

    @Override
    protected void tickCallBack() {

    }

    @Override
    public void restate() {

    }

    @Override
    public void setMessenger(IMessenger iMessenger) {
        this.iMessenger = iMessenger;
    }
}
