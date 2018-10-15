package stepping;

public interface Step {

      void init();

      boolean followsSubject(String subjectType);

      void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer);

      void tickCallBack();

      void restate();

      void shuttingDown();

      void setContainer(Container cntr);

      default StepConfig getLocalStepConfig(){
            return new StepConfig();
      }
}
