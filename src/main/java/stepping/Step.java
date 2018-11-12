package stepping;

public interface Step {

      void init(Container cntr);

      boolean followsSubject(String subjectType);

      void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer, Shutter shutter);

      default void tickCallBack(Shutter shutter) {
            throw new RuntimeException("tickCallBack not implemented");
      }

      void restate();

      void shuttingDown();

      default StepConfig getLocalStepConfig(){
            return new StepConfig();
      }
}
