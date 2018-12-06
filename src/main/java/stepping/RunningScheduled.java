package stepping;

import java.util.concurrent.*;

 class RunningScheduled extends IRunning {
     private long delay;
     private long initialdelay;
     private ScheduledFuture scheduledFuture;
     private TimeUnit timeUnit;
     private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

     protected RunningScheduled(long delay, long initialdelay, TimeUnit timeUnit, Runnable runnable) {
         this.delay = delay;
         this.initialdelay = initialdelay;
         this.runnable = runnable;
         this.timeUnit = timeUnit;
     }

     protected Future<?> awake() {
         this.scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, timeUnit);
         return scheduledFuture;
     }

     @Override
     public void close() {
         close(scheduledFuture, scheduledExecutorService);
     }
 }