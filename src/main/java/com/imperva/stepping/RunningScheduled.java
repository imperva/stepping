package com.imperva.stepping;

import java.util.concurrent.*;

 class RunningScheduled extends IRunning {
     private long delay;
     private long initialdelay;
     private ScheduledFuture scheduledFuture;
     private ScheduledExecutorService scheduledExecutorService;
     private TimeUnit timeUnit;
     protected RunningScheduled(long delay, long initialdelay, TimeUnit timeUnit, Runnable runnable) {
         this.delay = delay;
         this.initialdelay = initialdelay;
         this.runnable = runnable;
         this.timeUnit = timeUnit;
         this.scheduledExecutorService =  Executors.newSingleThreadScheduledExecutor();
     }

     protected Future<?> awake() {
         this.scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, timeUnit);
         return scheduledFuture;
     }

     ScheduledExecutorService getScheduledExecutorService(){
         return scheduledExecutorService;
     }

     @Override
     public void close() {
         close(scheduledFuture);
     }
 }