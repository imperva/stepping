package com.imperva.stepping;

class StepExternalConfig implements Step {
    private Step step;
    private StepConfig stepConfig;

    StepExternalConfig(Step step, StepConfig stepConfig) {
        this.step = step;
        this.stepConfig = stepConfig;
    }
    @Override
    public void init(Container cntr, Shouter shouter) {
        step.init(cntr, shouter);
    }

    @Override
    public boolean followsSubject(String subjectType) {
        return step.followsSubject(subjectType);
    }

    @Override
    public void listSubjectsToFollow(Follower follower) {
        step.listSubjectsToFollow(follower);
    }


    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        step.onSubjectUpdate(data, subjectType);
    }

    @Override
    public void onKill() {
        step.onKill();
    }

    @Override
    public void onTickCallBack() {
        step.onTickCallBack();
    }

    @Override
    public void onRestate(){ step.onRestate(); }

    @Override
    public StepConfig getConfig() {
        return stepConfig;
    }

}
