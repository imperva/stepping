package com.imperva.stepping;

public interface Step extends IIdentity {

      void init(Container cntr, Shouter shouter);

      default boolean followsSubject(String subjectType){ return true;}

      default void listSubjectsToFollow(Follower follower){}

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
