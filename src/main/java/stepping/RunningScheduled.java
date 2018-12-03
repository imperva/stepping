package stepping;

import java.util.concurrent.*;

 class RunningScheduled extends IRunning {
     private Integer delay;
     private Integer initialdelay;
     private String id;
     private ScheduledFuture scheduledFuture;
     private TimeUnit timeUnit;
     private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

     protected RunningScheduled(String id, int delay, int initialdelay, TimeUnit timeUnit, Runnable runnable) {
         this.id = id;
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