package stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.util.concurrent.*;

abstract class IRunning implements Closeable {
    static final Logger LOGGER = LoggerFactory.getLogger(IRunning.class);
    String id;
    Runnable runnable;
    protected abstract Future<?> awake();


    public void close(Future future, ExecutorService executorService) {
        try {
            LOGGER.info("Try close Stepping orchestrator gracefully. ID:" + id);
            if (future != null && !future.isDone() && !future.isCancelled()) {
                executorService.shutdownNow();
                LOGGER.info("Start Closing Stepping orchestrator Process");
                boolean isCanceled = future.cancel(true);
                LOGGER.trace("Stepping orchestrator Future canceled successfully?: " + isCanceled);
                //tickCallbackScheduledExecutorService.shutdownNow();
                LOGGER.trace("Stepping orchestrator ScheduledExecutorService shutted down");
                LOGGER.info("Finish closing Stepping orchestrator");
            }
        } catch (Exception e) {
            LOGGER.error("Failed closing Stepping orchestrator", e);
        }
    }
}
