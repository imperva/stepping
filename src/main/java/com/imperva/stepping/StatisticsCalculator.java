package com.imperva.stepping;

import java.util.*;

class StatisticsCalculator {
    private HashMap<String, List<StepsRuntimeMetadata>> stats = new HashMap<>();
    private List<StatisticsReport> statisticsReports = new ArrayList<StatisticsReport>();

    void add(String senderId, List<StepsRuntimeMetadata> stepsRuntimeMetadata) {
        stepsRuntimeMetadata.forEach(s -> {
            add(senderId, s);
        });
    }

    void add(String senderId, StepsRuntimeMetadata stepsRuntimeMetadata) {
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

            long avgProcessingTime = calcAvgProcessingTime(statData);
            double avgChunkSize = calcAvgChunkSize(statData);
            long latestQSize = calcLatestQSize(statData);

            StatisticsReport statisticsReport = new StatisticsReport();
            statisticsReport.setAvgChunkSize(avgChunkSize);
            statisticsReport.setAvgProcessingTime(avgProcessingTime);
            statisticsReport.setLatestQSize(latestQSize);
            statisticsReport.setStepSenderId(stepID);
            statisticsReports.add(statisticsReport);
            printStatistics(statisticsReport);
        }
        stats.clear();
        return new ArrayList<StatisticsReport>(statisticsReports);
    }

    private void printStatistics(StatisticsReport statisticsReport) {
        System.out.println("**** Step " + statisticsReport.getStepSenderId());
        System.out.println("Avg Chunk Size " + statisticsReport.getAvgChunkSize());
        System.out.println("Avg Process Time Size " + statisticsReport.getAvgProcessingTime());
        System.out.println("Q Size " + statisticsReport.getLatestQSize());
        System.out.println("****************");
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
        if (totTime == 0 || allChunkSize == 0)
            return 0;

        long avg = totTime / allChunkSize;
        return avg;

    }
}
