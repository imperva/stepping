package stepping;

import java.util.concurrent.*;

 class RunningScheduled extends IRunning {
     private long delay;
     private long initialdelay;
     private ScheduledFuture scheduledFuture;
     private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
     private TimeUnit timeUnit;

     protected RunningScheduled(String id, long delay, long initialdelay, TimeUnit timeUnit, Runnable runnable) {
         this.id = id;
         this.delay = delay;
         this.initialdelay = initialdelay;
         this.runnable = runnable;
         this.timeUnit = timeUnit;
     }

     protected Future<?> awake() {
         this.scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, timeUnit);
         scheduledExecutorServices.add(scheduledExecutorService);
         return scheduledFuture;
     }

     @Override
     public void close() {
         close(scheduledFuture);
     }
 }