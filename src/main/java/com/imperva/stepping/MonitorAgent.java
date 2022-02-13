package com.imperva.stepping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MonitorAgent {

    private List<StepsRuntimeMetadata> stepsRuntimeMetadataList = new ArrayList<>();
    private Shouter shouter;
    private long lastTime;
    private long timeout;
    private long startProcessingTime;
    private long stopProcessingTime;

    MonitorAgent(Shouter shouter, long timeout) {
        lastTime = System.currentTimeMillis();
        this.shouter = shouter;
        this.timeout = timeout;
    }

    void start(int chunkSize, int QSize) {
        startProcessingTime = System.currentTimeMillis();

        StepsRuntimeMetadata stepsRuntimeMetadata = new StepsRuntimeMetadata();
        stepsRuntimeMetadata.setStartTime(startProcessingTime);
        stepsRuntimeMetadata.setChunkSize(chunkSize);
        stepsRuntimeMetadata.setQSize(QSize);
        stepsRuntimeMetadataList.add(stepsRuntimeMetadata);
    }

    void stop() {
        stopProcessingTime = System.currentTimeMillis();
        StepsRuntimeMetadata stepsRuntimeMetadata = stepsRuntimeMetadataList.get(stepsRuntimeMetadataList.size() - 1);
        stepsRuntimeMetadata.setEndTime(stopProcessingTime);
        boolean isTimeExceeded = isTimeExceeded();
        if(isTimeExceeded) {
            sendMonitorReport();
            stepsRuntimeMetadataList = new ArrayList<>();
            lastTime = System.currentTimeMillis();
        }
    }

    private void sendMonitorReport() {
        shouter.shout(BuiltinSubjectType.STEPPING_RUNTIME_METADATA.name(), new Data(Collections.unmodifiableList(stepsRuntimeMetadataList)));
    }

    private boolean isTimeExceeded(){
        long gapTime = (System.currentTimeMillis() - lastTime) / 1000;
        boolean isTimeExceeded = gapTime >= timeout;
        return isTimeExceeded;
    }
}