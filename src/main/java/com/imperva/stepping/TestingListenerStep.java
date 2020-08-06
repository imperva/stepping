package com.imperva.stepping;

import java.util.List;
import java.util.function.BiFunction;

public class TestingListenerStep implements Step {
    private final List<String> subjects;
    private final BiFunction<Data, String, Boolean> onSubjectUpdateCallback;
    private StepConfig stepConfig;

    public TestingListenerStep(List<String> subjects, BiFunction<Data, String, Boolean> onSubjectUpdate, StepConfig stepConfig) {
        this.subjects = subjects;
        this.onSubjectUpdateCallback = onSubjectUpdate;
        this.stepConfig = stepConfig;
    }

    @Override
    public void init(Container cntr, Shouter shouter) {

    }

    @Override
    public void listSubjectsToFollow(Follower follower) {
        for (String sub : subjects) {
            follower.follow(sub);
        }
    }

    @Override
    public StepConfig getConfig() {
        StepConfig res = stepConfig == null ? new StepConfig() : stepConfig;
        /* todo BUG this should be for the registered steps.
        * need to create a decorator that overrides the config
        * */
        return res;
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        onSubjectUpdateCallback.apply(data, subjectType);

    }

    @Override
    public void onKill() {

    }
}
