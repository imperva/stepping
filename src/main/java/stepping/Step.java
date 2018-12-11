package stepping;

public interface Step {

      void init(Container cntr, Shouter shouter);

      boolean followsSubject(String subjectType);

      default StepConfig getConfig() {
            return new StepConfig();
      }

      void onSubjectUpdate(Data data, String subjectType);

      default void onTickCallBack() {
            throw new RuntimeException("onTickCallBack not implemented");
      }

      void onRestate();

      void onKill();
}
