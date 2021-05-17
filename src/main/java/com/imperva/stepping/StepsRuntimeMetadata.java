package com.imperva.stepping;

import java.util.Date;

public class StepsRuntimeMetadata {
    private Date startTime;
    private Date endTime;
    private long QSize;
    private long chunkSize;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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
