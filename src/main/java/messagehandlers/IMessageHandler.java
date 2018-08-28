//package messagehandlers;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import properties.PropertiesReader;
//
//import java.io.Closeable;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public abstract class IMessageHandler implements Runnable , Closeable {
//    private static final Logger LOGGER = LoggerFactory.getLogger(IMessageHandler.class);
//
//    private ScheduledFuture scheduledFuture;
//    private ScheduledExecutorService scheduledExecutorService;
//
//
//    public IMessageHandler() {
//        init();
//    }
//
//    protected void init() {
//        synchronized (IMessageHandler.class) {
//            if (scheduledFuture == null) {
//                ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor(r -> {
//                    Thread t = Executors.defaultThreadFactory().newThread(r);
//                    t.setDaemon(true);
//                    t.setContextClassLoader(null);
//                    t.setName("grouping-main-loop");
//                    return t;
//                });
////                LOGGER.info("init grouping main loop");
//                PropertiesReader properties = PropertiesReader.getInstance();
//                Integer initialDelay = Integer.valueOf(properties.getProperty("orchestrator.main.loop.initial.delay"));
//                Integer delay = Integer.valueOf(properties.getProperty("orchestrator.main.loop.delay"));
//                this.scheduledFuture = es.scheduleWithFixedDelay(this::run, initialDelay, delay, TimeUnit.MILLISECONDS);
//                this.scheduledExecutorService = es;
//            }
//        }
//    }
//
//    @Override
//    public void close() {
//        try {
//            LOGGER.info("Try close Grouping orchestrator gracefully");
//            if (scheduledFuture != null && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
//                LOGGER.trace("Start Closing Grouping orchestrator Process");
//                boolean isCanceled = scheduledFuture.cancel(true);
//                LOGGER.trace("Grouping orchestrator Future canceled successfully?: " + isCanceled);
//                scheduledExecutorService.shutdownNow();
//                LOGGER.trace("Grouping orchestrator ScheduledExecutorService shutted down");
//                LOGGER.info("Finish closing Grouping orchestrator");
////                clear();
//            }
//        } catch (Exception e) {
//            LOGGER.error("Failed closing Grouping orchestrator", e);
//        }
//    }
//
//}
