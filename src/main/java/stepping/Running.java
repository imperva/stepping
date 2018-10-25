package stepping;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Running extends IRunning {

    private Future future;
    static ExecutorService executorService = Executors.newCachedThreadPool();

    protected Running(String id, Runnable runnable) {
        this.id = id;
        this.runnable = runnable;
    }

    protected Future<?> awake() {
        if (runnable != null) {
            this.future = executorService.submit(runnable);
            return future;
        }
        return null;
    }

    @Override
    public void close() {
        close(future, executorService);
    }
}









//                            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, r -> {
//                                Thread t = Executors.defaultThreadFactory().newThread(r);
//                                t.setDaemon(daemon);
//                                t.setContextClassLoader(null);
//                                t.setName(id);
//                                return t;
//                            });


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