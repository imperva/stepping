package stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

abstract class IRunning implements Closeable {
    static final Logger logger = LoggerFactory.getLogger(IRunning.class);
    String id;
    Runnable runnable;
    protected static ExecutorService executorService = Executors.newCachedThreadPool();
    protected static List<ScheduledExecutorService> scheduledExecutorServices = new ArrayList<>();
    private static Object lock = new Object();

    protected abstract Future<?> awake();

    public static void kill() {
        synchronized (lock) {
            try {
                if (!executorService.isShutdown()) {
                    logger.info("Closing ExecutorService gracefully");
                    executorService.shutdownNow();
                }

                for (ExecutorService scheduledExecutorService : scheduledExecutorServices) {
                    if (!scheduledExecutorService.isShutdown()) {
                        logger.info("Closing ScheduledExecutorService gracefully");
                        scheduledExecutorService.shutdownNow();
                    }
                }
            } catch (Exception e) {

            }
        }
    }


    public void close(Future future) {
        try {

            if (future != null && !future.isDone() && !future.isCancelled()) {
                logger.info("@@@@@ Start Closing Stepping Future - " + id);
                boolean isCanceled = future.cancel(true);
                logger.trace("Stepping orchestrator Future canceled successfully?: " + isCanceled);
                logger.info("Finish closing Stepping orchestrator");
            }
        } catch (Exception e) {
            logger.error("Failed closing Stepping orchestrator", e);
        }
    }
}
