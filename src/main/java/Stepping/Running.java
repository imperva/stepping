package Stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Running implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Running.class);

    private ScheduledFuture scheduledFuture;
    private ScheduledExecutorService scheduledExecutorService;
    private int delay;
    private int initialdelay;
    private String id;
    private boolean daemon;
    private Runnable runnable;

//    protected Running(String id, Runnable runnable) { this(id, runnable,100,100,false);
//
//    }

    protected Running(String id, Runnable runnable, int delay, int initialdelay, boolean daemon) {
        this.id = id;
        this.delay = delay;
        this.initialdelay = initialdelay;
        this.daemon = daemon;
        this.runnable = runnable;
    }

    protected void awake() {
        synchronized (Running.class) {
            if (scheduledFuture == null) {
                ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(daemon);
                    t.setContextClassLoader(null);
                    t.setName(id);
                    return t;
                });

                this.scheduledFuture = es.scheduleWithFixedDelay(this.runnable::run, initialdelay, delay, TimeUnit.MILLISECONDS);
                this.scheduledExecutorService = es;
            }
        }
    }

    @Override
    public void close() {
        try {
            LOGGER.info("Try close Stepping orchestrator gracefully. ID:" + id);
            if (scheduledFuture != null && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
                LOGGER.info("Start Closing Stepping orchestrator Process");
                boolean isCanceled = scheduledFuture.cancel(true);
                LOGGER.trace("Stepping orchestrator Future canceled successfully?: " + isCanceled);
                scheduledExecutorService.shutdownNow();
                LOGGER.trace("Stepping orchestrator ScheduledExecutorService shutted down");
                LOGGER.info("Finish closing Stepping orchestrator");
            }
        } catch (Exception e) {
            LOGGER.error("Failed closing Stepping orchestrator", e);
        }
    }
}
