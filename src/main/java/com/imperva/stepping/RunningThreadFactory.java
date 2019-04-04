//package stepping;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.ThreadFactory;
//
//public class RunningThreadFactory implements ThreadFactory {
//    static final Logger logger = LoggerFactory.getLogger(RunningThreadFactory.class);
//    @Override
//    public Thread newThread(Runnable r) {
//
//        Thread t = new Thread(r);
//        t.setDaemon(false);
//        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                logger.error("UncaughtException detected in thread " + t.getId(), e);
//            }
//        });
//        t.start();
//        return t;
//    }
//}
