package stepping;

public interface Step {

      void init(Container cntr);

      boolean followsSubject(String subjectType);

      void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer);

      void tickCallBack();

      void restate();

      void shuttingDown();

      default StepConfig getLocalStepConfig(){
            return new StepConfig();
      }
}
