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