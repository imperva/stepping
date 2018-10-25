package stepping;

import java.util.concurrent.*;

public class RunningScheduled extends IRunning {
    private Integer delay;
    private Integer initialdelay;
    private String id;
    private ScheduledFuture scheduledFuture;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    protected RunningScheduled(String id, int delay, int initialdelay, Runnable runnable) {
        this.id = id;
        this.delay = delay;
        this.initialdelay = initialdelay;
        this.runnable = runnable;
    }

    protected Future<?> awake() {
        this.scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    @Override
    public void close() {
        close(scheduledFuture, scheduledExecutorService);
    }
}