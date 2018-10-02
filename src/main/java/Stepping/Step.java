package Stepping;

public interface Step {

      void init();

      boolean isAttach(String subjectType);

      void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer);

      void tickCallBack();

      void restate();

      void shuttingDown();

      void setContainer(Container cntr);

      default StepConfig getLocalStepConfig(){
            return null;
      }
}
