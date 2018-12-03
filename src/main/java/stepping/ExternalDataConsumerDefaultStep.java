//package stepping;
//
//import com.google.gson.JsonObject;
//
//import java.util.List;
//
//public class ExternalDataConsumerDefaultStep implements Step {
//
//    private IMessenger iMessenger;
//    private Container container;
//
//
//    public ExternalDataConsumerDefaultStep() {
//       // super(ExternalDataConsumerDefaultStep.class.getName());
//    }
//
//    @Override
//    public void init() {
//
//    }
//
//    @Override
//    public boolean followsSubject(String subjectType) {
//        return false;
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
//    @Override
//    public void onSubjectUpdate(Data data, SubjectContainer subjectContainer) {
//
//    }
//
//    @Override
//    public void onTickCallBack() {
//        Data data = iMessenger.fetching();
//        if (data.getValue() != null) {
//            SubjectContainer subjectContainer = container.getById(BuiltinTypes.STEPPING_SUBJECT_CONTAINER.name());
//            subjectContainer.<List<JsonObject>>getByName(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name()).setData(data);
//        } else {
//            System.out.println("No data received from external resource");
//        }
//    }
//
//    public void setMessenger(IMessenger iMessenger) {
//        this.iMessenger = iMessenger;
//    }
//
//   @Override
//   public StepConfig getConfig(){
//       StepConfig stepConfig = new StepConfig();
//       stepConfig.setEnableDecelerationStrategy(false);
//       return stepConfig;
//   }
//
//}
