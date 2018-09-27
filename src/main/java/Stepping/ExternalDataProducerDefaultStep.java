package Stepping;

public class ExternalDataProducerDefaultStep implements Step {

    private IMessenger iMessenger;
    private Container container;

    public ExternalDataProducerDefaultStep() {
       // super(ExternalDataProducerDefaultStep.class.getName());
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjecType) {
        if (DefaultSubjectType.S_PUBLISH_DATA.name().equals(subjecType)) {
            return true;
        }
        return false;
    }


    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.S_PUBLISH_DATA.name().equals(subject.getType())) {
            iMessenger.emit(subject.getData());
        }
    }

    @Override
    public void tickCallBack() {

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
    public StepConfig getStepConfig() {
        return null;
    }

    public void setMessenger(IMessenger iMessenger) {
        this.iMessenger = iMessenger;
    }
}
