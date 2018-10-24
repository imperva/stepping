package stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.*;

public class Running implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Running.class);

    private ScheduledFuture tickCallBackScheduledFuture;
    private ScheduledExecutorService tickCallbackScheduledExecutorService;
    private ScheduledFuture algoTickCallBackScheduledFuture;
    private ScheduledExecutorService algoTickCallbackScheduledExecutorService;
    private int delay;
    private int initialdelay;
    private String id;
    private boolean daemon;
    private IStepDecorator step;
    private IAlgoDecorator algo;


    protected Running(String id, IStepDecorator step, int delay, int initialdelay, boolean daemon) {
        this.id = id;
        this.delay = delay;
        this.initialdelay = initialdelay;
        this.daemon = daemon;
        this.step = step;
    }

    protected Running(String id, IAlgoDecorator algo, int delay, int initialdelay, boolean daemon) {
        this.id = id;
        this.delay = delay;
        this.initialdelay = initialdelay;
        this.daemon = daemon;
        this.algo = algo;
    }

    protected void awake() {
        if (step != null) {
            ExecutorService x = Executors.newFixedThreadPool(1);
            x.submit(step::dataListener);


            if (step.getLocalStepConfig().isEnableTickCallback()) {
                synchronized (Running.class) {
                    if (tickCallBackScheduledFuture == null) {
                        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor(r -> {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(daemon);
                            t.setContextClassLoader(null);
                            t.setName(id);
                            return t;
                        });

                        this.tickCallBackScheduledFuture = es.scheduleWithFixedDelay(this.step::tickCallBack, initialdelay, delay, TimeUnit.MILLISECONDS);
                        this.tickCallbackScheduledExecutorService = es;
                    }
                }
            }
        }

        if(algo != null){
            synchronized (Running.class) {
                if (tickCallBackScheduledFuture == null) {
                    ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor(r -> {
                        Thread t = Executors.defaultThreadFactory().newThread(r);
                        t.setDaemon(daemon);
                        t.setContextClassLoader(null);
                        t.setName(id);
                        return t;
                    });

                    this.algoTickCallBackScheduledFuture = es.scheduleWithFixedDelay(this.algo::tickCallBack, initialdelay, delay, TimeUnit.MILLISECONDS);
                    this.algoTickCallbackScheduledExecutorService = es;
                }
            }
        }
    }

    @Override
    public void close() {
        try {
            LOGGER.info("Try close Stepping orchestrator gracefully. ID:" + id);
            if (tickCallBackScheduledFuture != null && !tickCallBackScheduledFuture.isDone() && !tickCallBackScheduledFuture.isCancelled()) {
                LOGGER.info("Start Closing Stepping orchestrator Process");
                boolean isCanceled = tickCallBackScheduledFuture.cancel(true);
                LOGGER.trace("Stepping orchestrator Future canceled successfully?: " + isCanceled);
                tickCallbackScheduledExecutorService.shutdownNow();
                LOGGER.trace("Stepping orchestrator ScheduledExecutorService shutted down");
                LOGGER.info("Finish closing Stepping orchestrator");
            }
        } catch (Exception e) {
            LOGGER.error("Failed closing Stepping orchestrator", e);
        }
    }
}
