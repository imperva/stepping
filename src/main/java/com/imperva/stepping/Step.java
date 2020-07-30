package com.imperva.stepping;

public interface Step extends IIdentity {

      void init(Container cntr, Shouter shouter);

      default boolean followsSubject(String subjectType){ return false;}

      default void listSubjectsToFollow(Follower follower){}

      default StepConfig getConfig() {
            return new StepConfig();
      }

      default void onSubjectUpdate(Data data, String subjectType){ }

      default void onTickCallBack() {
            throw new SteppingSystemException("onTickCallBack not implemented");
      }

      default void onRestate(){ }

      void onKill();
}
