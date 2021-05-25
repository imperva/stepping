package com.imperva.stepping;

import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class MonitorAgent {

    private StopWatch runtimeMetadataStopWatch;
    private List<StepsRuntimeMetadata> stepsRuntimeMetadataList = new ArrayList<>();
    private Shouter shouter;
    private long lastTime;
    private long timeout;

    MonitorAgent(Shouter shouter, long timeout) {
        runtimeMetadataStopWatch = new StopWatch();
        lastTime = System.currentTimeMillis();
        this.shouter = shouter;
        this.timeout = timeout;
    }

    void start(int chunkSize) {
        runtimeMetadataStopWatch.start();

        StepsRuntimeMetadata stepsRuntimeMetadata = new StepsRuntimeMetadata();
        stepsRuntimeMetadata.setStartTime(runtimeMetadataStopWatch.getStartTime());
        stepsRuntimeMetadata.setChunkSize(chunkSize);

        stepsRuntimeMetadataList.add(stepsRuntimeMetadata);
    }

    void stop() {
        runtimeMetadataStopWatch.stop();

        StepsRuntimeMetadata stepsRuntimeMetadata = stepsRuntimeMetadataList.get(0);
        stepsRuntimeMetadata.setEndTime(runtimeMetadataStopWatch.getStopTime());
        boolean isTimeExceeded = isTimeExceeded();
        if(isTimeExceeded) {
            sendMonitorReport();
            stepsRuntimeMetadataList.clear();
            lastTime = System.currentTimeMillis();
        }
        runtimeMetadataStopWatch.reset();
    }

    private void sendMonitorReport() {
        shouter.shout(BuiltinSubjectType.STEPPING_RUNTIME_METADATA.name(), new Data(Collections.unmodifiableList(stepsRuntimeMetadataList)));
        stepsRuntimeMetadataList = new ArrayList<>();
    }

    private boolean isTimeExceeded(){
        boolean isTimeExceeded = (System.currentTimeMillis() - lastTime) / 1000 >= timeout;
        System.out.println("isTimeExceeded: " + isTimeExceeded);
        System.out.println("Calculated time:" + (System.currentTimeMillis() - lastTime) / 1000);
        System.out.println("timeout: " + timeout);
        return isTimeExceeded;
    }
}