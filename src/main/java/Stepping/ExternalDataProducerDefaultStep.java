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
    public boolean isAttach(String subjectType) {
        if (DefaultSubjectType.STEPPING_PUBLISH_DATA.name().equals(subjectType)) {
            return true;
        }
        return false;
    }


    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.STEPPING_PUBLISH_DATA.name().equals(subject.getType())) {
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


    public void setMessenger(IMessenger iMessenger) {
        this.iMessenger = iMessenger;
    }
}
