package com.imperva.stepping;

import java.util.Date;

public class StepsRuntimeMetadata {
    private long startTime;
    private long endTime;
    private long QSize;
    private long chunkSize;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getQSize() {
        return QSize;
    }

    public void setQSize(long QSize) {
        this.QSize = QSize;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }
}
