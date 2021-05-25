package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class StatisticsCalculator {
    private final Logger logger = LoggerFactory.getLogger(StatisticsCalculator.class);
    private HashMap<String, List<StepsRuntimeMetadata>> stats = new HashMap<>();
    private List<StatisticsReport> statisticsReports = new ArrayList<StatisticsReport>();


    void add(String senderId, List<StepsRuntimeMetadata> stepsRuntimeMetadata) {
        stepsRuntimeMetadata.forEach(s -> {
            add(senderId, s);
        });
    }

    void add(String senderId, StepsRuntimeMetadata stepsRuntimeMetadata) {
        logger.debug("Adding metadata to StatisticsCalculator: " + senderId);
        List<StepsRuntimeMetadata> messages = stats.get(senderId);
        if (messages == null) {
            List<StepsRuntimeMetadata> stepsRuntimeMetadataList = new ArrayList<>();
            stepsRuntimeMetadataList.add(stepsRuntimeMetadata);
            stats.put(senderId, stepsRuntimeMetadataList);
        } else {
            messages.add(stepsRuntimeMetadata);
        }
    }

    List<StatisticsReport> calculate() {
        Iterator<Map.Entry<String, List<StepsRuntimeMetadata>>> it = stats.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry<String, List<StepsRuntimeMetadata>> pair = it.next();
            String stepID = pair.getKey();
            List<StepsRuntimeMetadata> statData = pair.getValue();
            logger.debug("calculating stata for: " + stepID);
            long avgProcessingTime = calcAvgProcessingTime(statData);
            int avgChunkSize = calcAvgChunkSize(statData);
            long latestQSize = calcLatestQSize(statData);

            StatisticsReport statisticsReport = new StatisticsReport();
            statisticsReport.setAvgChunkSize(avgChunkSize);
            statisticsReport.setAvgProcessingTime(avgProcessingTime);
            statisticsReport.setLatestQSize(latestQSize);
            statisticsReport.setStepSenderId(stepID);
            statisticsReports.add(statisticsReport);

            String reportPrint = statisticsReport.toString();
            logger.debug(reportPrint);
        }
        stats.clear();


        List<StatisticsReport> statisticsReportsResult = statisticsReports;
        statisticsReports = new ArrayList<>();
        return statisticsReportsResult;
    }

    private long calcLatestQSize(List<StepsRuntimeMetadata> statData) {
        return statData.get(statData.size() - 1).getQSize();
    }

    private int calcAvgChunkSize(List<StepsRuntimeMetadata> statData) {
        return (int)statData.stream().mapToLong(xx -> xx.getChunkSize()).average().getAsDouble();
    }

    private long calcAvgProcessingTime(List<StepsRuntimeMetadata> statData) {
        long allChunkSize = statData.stream().mapToLong(StepsRuntimeMetadata::getChunkSize).sum();
        long starttime = statData.get(statData.size() - 1).getStartTime();
        long endtime = statData.get(0).getEndTime();

        long totTime = endtime - starttime;
        if (totTime == 0 || allChunkSize == 0)
            return 0;

        long avg = totTime / allChunkSize;
        return avg / 1000;

    }
}
