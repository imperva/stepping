package Stepping;

public interface  Step {

      void init();

      boolean isAttach(String subjecType);

      void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer);

      void tickCallBack();

      void restate();

      void shuttingDown();

      void setContainer(Container cntr);

      StepConfig getStepConfig();
}
