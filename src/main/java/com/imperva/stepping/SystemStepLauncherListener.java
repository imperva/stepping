package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;

@SystemStep
class SystemStepLauncherListener implements Step {
    private final Logger logger = LoggerFactory.getLogger(SystemStepLauncherListener.class);
    private final List<String> subjects;
    private final BiFunction<Data, String, Boolean> onSubjectUpdateCallback;

    SystemStepLauncherListener(List<String> subjects, BiFunction<Data, String, Boolean> onSubjectUpdate) {
        this.subjects = subjects;
        this.onSubjectUpdateCallback = onSubjectUpdate;
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
    public void onSubjectUpdate(Data data, String subjectType) {
        onSubjectUpdateCallback.apply(data, subjectType);

    }

    @Override
    public void onKill() {
        logger.info("Stopping LauncherListenerStep");
        boolean stopped = onSubjectUpdateCallback.apply(null,"LAUNCHER-POISON-PILL");
        logger.info("LauncherListenerStep Stopped successfully: " + stopped);

    }

    @Override
    public String getId() {
        return "SYSTEM_STEP_LAUNCHER_LISTENER";
    }

}
