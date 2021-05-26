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
            double avgProcessingTime = calcAvgProcessingTime(statData);
            long avgChunkSize = calcAvgChunkSize(statData);
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

    private long calcAvgChunkSize(List<StepsRuntimeMetadata> statData) {
        return Math.round(statData.stream().mapToLong(xx -> xx.getChunkSize()).average().getAsDouble());
    }

    private double calcAvgProcessingTime(List<StepsRuntimeMetadata> statData) {
        List<Long> totTimes = new ArrayList<>();
        statData.forEach(meta->{
            long starttime = meta.getStartTime();
            long endtime = meta.getEndTime();
            long total =  endtime - starttime;
            totTimes.add(total);
        });

        long avgTimes = Math.round(totTimes.stream().mapToLong(s->s).average().getAsDouble());
        if (avgTimes == 0)
            return 0;

        double seconds = avgTimes / 1000.0;
        return seconds;

    }
}
