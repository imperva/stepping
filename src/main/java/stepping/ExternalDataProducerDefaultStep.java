//package stepping;
//
//public class ExternalDataProducerDefaultStep implements Step {
//
//    private IMessenger iMessenger;
//    private Container container;
//
//    public ExternalDataProducerDefaultStep() {
//       // super(ExternalDataProducerDefaultStep.class.getName());
//    }
//
//    @Override
//    public void init() {
//
//    }
//
//    @Override
//    public boolean followsSubject(String subjectType) {
//        if (BuiltinSubjectType.STEPPING_PUBLISH_DATA.name().equals(subjectType)) {
//            return true;
//        }
//        return false;
//    }
//
//
//    @Override
//    public void onSubjectUpdate(Data data, SubjectContainer subjectContainer) {
//        if (BuiltinSubjectType.STEPPING_PUBLISH_DATA.name().equals(data.getSubjectType())) {
//            iMessenger.emit(data);
//        }
//    }
//
//    @Override
//    public void onTickCallBack() {
//
//    }
//
//    @Override
//    public void onRestate() {
//
//    }
//
//    @Override
//    public void onKill() {
//
//    }
//
//    @Override
//    public void setContainer(Container cntr) {
//        this.container = cntr;
//    }
//
//
//    public void setMessenger(IMessenger iMessenger) {
//        this.iMessenger = iMessenger;
//    }
//}
