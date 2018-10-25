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

//
//    protected IRunning(String id, IStepDecorator step, int delay, int initialdelay, boolean daemon, Runnable runnable) {
//        this.id = id;
//        this.delay = delay;
//        this.initialdelay = initialdelay;
//        this.daemon = daemon;
//        //this.step = step;
//        this.runnable = runnable;
//    }

//    protected void awake() {
//
//            if (runnable != null) {
//                if (delay == null) {
//
//
//                    executorService.submit(runnable);
//
//
////                    Thread t = new Thread(rnble);
////                    t.setName(id);
////                    t.setDaemon(daemon);
////                    t.start();
//                } else {
//
//
////                            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, r -> {
////                                Thread t = Executors.defaultThreadFactory().newThread(r);
////                                t.setDaemon(daemon);
////                                t.setContextClassLoader(null);
////                                t.setName(id);
////                                return t;
////                            });
//
//                            this.tickCallBackScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, TimeUnit.MILLISECONDS);
//                            this.executorService = scheduledExecutorService;
//
//
//
//                }


//                if (step.getLocalStepConfig().isEnableTickCallback()) {
//                    synchronized (IRunning.class) {
//                        if (tickCallBackScheduledFuture == null) {
//                            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
//                                Thread t = Executors.defaultThreadFactory().newThread(r);
//                                t.setDaemon(daemon);
//                                t.setContextClassLoader(null);
//                                t.setName(id);
//                                return t;
//                            });
//
//                            this.tickCallBackScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this.step::tickCallBack, initialdelay, delay, TimeUnit.MILLISECONDS);
//                            this.tickCallbackScheduledExecutorService = scheduledExecutorService;
//                        }
//                    }
//                }
//            }
//        }

//        if(algo != null){
//            synchronized (IRunning.class) {
//                if (tickCallBackScheduledFuture == null) {
//                    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
//                        Thread t = Executors.defaultThreadFactory().newThread(r);
//                        t.setDaemon(daemon);
//                        t.setContextClassLoader(null);
//                        t.setName(id);
//                        return t;
//                    });
//
//                    this.algoTickCallBackScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this.algo::tickCallBack, initialdelay, delay, TimeUnit.MILLISECONDS);
//                    this.algoTickCallbackScheduledExecutorService = scheduledExecutorService;
//                }
//            }
//        }


    //      }
    //  }
//    @Override
//    public void close() {
//        try {
//            LOGGER.info("Try close Stepping orchestrator gracefully. ID:" + id);
//            if (tickCallBackScheduledFuture != null && !tickCallBackScheduledFuture.isDone() && !tickCallBackScheduledFuture.isCancelled()) {
//                LOGGER.info("Start Closing Stepping orchestrator Process");
//                boolean isCanceled = tickCallBackScheduledFuture.cancel(true);
//                LOGGER.trace("Stepping orchestrator Future canceled successfully?: " + isCanceled);
//                //tickCallbackScheduledExecutorService.shutdownNow();
//                LOGGER.trace("Stepping orchestrator ScheduledExecutorService shutted down");
//                LOGGER.info("Finish closing Stepping orchestrator");
//            }
//        } catch (Exception e) {
//            LOGGER.error("Failed closing Stepping orchestrator", e);
//        }
//    }
}
