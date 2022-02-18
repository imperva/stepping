package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SystemStep
class SystemStepMonitor implements Step {

    private final Logger logger = LoggerFactory.getLogger(SystemStepMonitor.class);
    private int reportReleaseTimeout;//Seconds
    private Container cntr;
    private Shouter shouter;
    private List<Subject> subjects;
    private List<VisualStep> stepsInfo;
    private Visualizer visualizer;
    private StatisticsCalculator statisticsCalculator;

    @Override
    public void init(Container cntr, Shouter shouter) {
        this.cntr = cntr;
        this.shouter = shouter;
        subjects = ((Container) (cntr.getById("__STEPPING_PRIVATE_CONTAINER__"))).getTypeOf(Subject.class);
        stepsInfo = ((Container) (cntr.getById("__STEPPING_PRIVATE_CONTAINER__"))).<StepDecorator>getTypeOf(StepDecorator.class).stream().map(s-> new VisualStep(s.getStep().getId(), s.getId(),s.getDistributionNodeID(), s.getConfig().getNumOfNodes(), false)).collect(Collectors.toList());

    }

    @Override
    public void onRestate() {
        visualizer = new Visualizer(subjects, stepsInfo);
        visualizer.initVisualization();
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
        logger.debug("Adding metadata for: " + data.getSenderId());
        statisticsCalculator.add(data.getSenderId(), stepsRuntimeMetadata);
    }

    @Override
    public void onTickCallBack() {
        List<StatisticsReport> statisticsReports = statisticsCalculator.calculate();
        logger.debug("Monitor onTickCallBack: " + statisticsReports.size());
        if(statisticsReports.size() > 0)
            visualizer.updateMetadata(statisticsReports);
        if(!statisticsReports.isEmpty())
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
        visualizer.viewClosed("");
    }

    @Override
    public String getId() {
        return "SYSTEM_STEP_MONITOR";
    }
}