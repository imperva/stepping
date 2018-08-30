package Stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.PropertiesReader;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class IRunning implements Runnable , Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRunning.class);

    private ScheduledFuture scheduledFuture;
    private ScheduledExecutorService scheduledExecutorService;
    private int delay = 10;
    private int initialdelay = 10;
    private String id;

    protected IRunning(String id, int delay, int initialdelay) {
        this.id = id;
        this.delay = delay;
        this.initialdelay = initialdelay;
    }

    protected void go() {
        synchronized (IRunning.class) {
            if (scheduledFuture == null) {
                ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    t.setContextClassLoader(null);
                    t.setName(id);
                    return t;
                });
//                LOGGER.info("init grouping main loop");
                PropertiesReader properties = PropertiesReader.getInstance();

                this.scheduledFuture = es.scheduleWithFixedDelay(this::run, initialdelay, delay, TimeUnit.MILLISECONDS);
                this.scheduledExecutorService = es;
            }
        }
    }

    @Override
    public void close() {
        try {
            LOGGER.info("Try close Grouping orchestrator gracefully");
            if (scheduledFuture != null && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
                LOGGER.trace("Start Closing Grouping orchestrator Process");
                boolean isCanceled = scheduledFuture.cancel(true);
                LOGGER.trace("Grouping orchestrator Future canceled successfully?: " + isCanceled);
                scheduledExecutorService.shutdownNow();
                LOGGER.trace("Grouping orchestrator ScheduledExecutorService shutted down");
                LOGGER.info("Finish closing Grouping orchestrator");
            }
        } catch (Exception e) {
            LOGGER.error("Failed closing Grouping orchestrator", e);
        }
    }
}
