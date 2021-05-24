package com.imperva.stepping;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SystemStep
class SystemStepMonitor implements Step {
    private int reportReleaseTimeout = 60; //Seconds
    private Container cntr;
    private Shouter shouter;
    private List<Subject> subjects;
    private Visualizer visualizer;
    private StatisticsCalculator statisticsCalculator;

    @Override
    public void init(Container cntr, Shouter shouter) {
        this.cntr = cntr;
        this.shouter = shouter;
        subjects = ((Container) (cntr.getById("__STEPPING_PRIVATE_CONTAINER__"))).getTypeOf(Subject.class);
        visualizer = new Visualizer(subjects);
        statisticsCalculator = new StatisticsCalculator();
    }

    @Override
    public boolean followsSubject(String subjectType) {
        return true;
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        if (subjectType.equals(BuiltinSubjectType.STEPPING_RUNTIME_METADATA.name())) {
            collectMetadata(data, subjectType);
        } else {
            interceptReceiver(data, subjectType);
        }
    }

    private void interceptReceiver(Data data, String subjectType) {
        visualizer.draw(data.getSenderId(), subjectType);
    }

    void collectMetadata(Data data, String subjectType) {
        List<StepsRuntimeMetadata> stepsRuntimeMetadata = (List<StepsRuntimeMetadata>) data.getValue();
        statisticsCalculator.add(data.getSenderId(), stepsRuntimeMetadata);
    }

    @Override
    public void onTickCallBack() {
        List<StatisticsReport> statisticsReports = statisticsCalculator.calculate();
        if (!statisticsReports.isEmpty())
            shouter.shout(BuiltinSubjectType.STEPPING_STEPS_STATISTICS_READY.name(), new Data(statisticsReports));
    }

    void setReportReleaseTimeout(int timeout){
        reportReleaseTimeout = timeout;
    }

    @Override
    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelay(reportReleaseTimeout);
        stepConfig.setRunningPeriodicDelayUnit(TimeUnit.SECONDS);
        stepConfig.setDistributionStrategy(new OfferAll2AllDistributionStrategy());
        return stepConfig;
    }

    @Override
    public void onKill() {

    }

    @Override
    public String getId() {
        return "SYSTEM_STEP_MONITOR";
    }
}