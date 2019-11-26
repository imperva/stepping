package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class RunnersController {
    private final Logger logger = LoggerFactory.getLogger(RunnersController.class);
    private final Object lock = new Object();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<ScheduledExecutorService> scheduledExecutorServices = new CopyOnWriteArrayList<>();

    ExecutorService getExecutorService() {
        return executorService;
    }

    void addScheduledRunner(ScheduledExecutorService scheduledExecutorService) {
        scheduledExecutorServices.add(scheduledExecutorService);
    }

    void kill() {
        synchronized (lock) {
            try {
                if (!executorService.isShutdown()) {
                    logger.info("Closing ExecutorService gracefully");
                    executorService.shutdown();
                    executorService.shutdownNow();
                }
                for (ExecutorService scheduledExecutorService : scheduledExecutorServices) {
                    if (!scheduledExecutorService.isShutdown()) {
                        logger.info("Closing ScheduledExecutorService gracefully");
                        scheduledExecutorService.shutdown();
                        scheduledExecutorService.shutdownNow();
                    }
                }
                logger.info("Tasks are dead");
            } catch (Exception e) {
                logger.error("Failed closing ScheduledExecutorService");
            } finally {
                scheduledExecutorServices.clear();
            }
        }
    }
}
