package com.imperva.stepping;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SystemStep
class SystemStepStatistics implements Step {
    private Container cntr;
    private Shouter shouter;
    private HashMap<String, List<StepsRuntimeMetadata>> stats = new HashMap<>();

    @Override
    public void init(Container cntr, Shouter shouter) {

        this.cntr = cntr;
        this.shouter = shouter;
    }

    @Override
    public void listSubjectsToFollow(Follower follower) {
        follower.follow(BuiltinSubjectType.STEPPING_RUNTIME_METADATA.name(), this::statsArrived);
    }

    void statsArrived(Data data) {
        List<StepsRuntimeMetadata> messages = stats.get(data.getSenderId());
        StepsRuntimeMetadata stepsRuntimeMetadata = (StepsRuntimeMetadata) data.getValue();
        if (messages == null) {
            List<StepsRuntimeMetadata> stepsRuntimeMetadataList = new ArrayList<>();
            stepsRuntimeMetadataList.add(stepsRuntimeMetadata);
            stats.put(data.getSenderId(), stepsRuntimeMetadataList);
        } else {
            messages.add(stepsRuntimeMetadata);
        }
    }

    @Override
    public void onTickCallBack() {
        Iterator<Map.Entry<String, List<StepsRuntimeMetadata>>> it = stats.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<StepsRuntimeMetadata>> pair = it.next();
            String stepID = pair.getKey();
            List<StepsRuntimeMetadata> statData = pair.getValue();

            long avgProcessingTime = calcAvgProcessingTime(statData);
            double avgChunkSize = calcAvgChunkSize(statData);
            long latestQSize = calcLatestQSize(statData);
            StatisticsReport statisticsReport = new StatisticsReport();
            statisticsReport.setAvgChunkSize(avgChunkSize);
            statisticsReport.setAvgProcessingTime(avgProcessingTime);
            statisticsReport.setLatestQSize(latestQSize);

            shouter.shout(BuiltinSubjectType.STEPPING_STEPS_STATISTICS_READY.name(), statisticsReport);
        }


        stats.clear();
    }

    private long calcLatestQSize(List<StepsRuntimeMetadata> statData) {
        return statData.get(statData.size() - 1).getQSize();
    }

    private double calcAvgChunkSize(List<StepsRuntimeMetadata> statData) {
        return statData.stream().mapToLong(xx -> xx.getChunkSize()).average().getAsDouble();
    }

    private long calcAvgProcessingTime(List<StepsRuntimeMetadata> statData) {
        long allChunkSize = statData.stream().mapToLong(StepsRuntimeMetadata::getChunkSize).sum();
        long starttime = statData.get(0).getStartTime();
        long endtime = statData.get(statData.size() - 1).getEndTime();

        long totTime = endtime - starttime;
        long avg =  totTime / allChunkSize;
        return avg;

    }

    @Override
    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelay(30);
        stepConfig.setRunningPeriodicDelayUnit(TimeUnit.SECONDS);
        return stepConfig;
    }

    @Override
    public void onKill() {

    }

    @Override
    public String getId() {
        return "SYSTEM_STEP_STATISTICS";
    }
}
